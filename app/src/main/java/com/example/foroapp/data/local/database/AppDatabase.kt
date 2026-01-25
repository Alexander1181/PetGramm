package com.example.foroapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.foroapp.data.local.post.PostDao
import com.example.foroapp.data.local.post.PostEntity
import com.example.foroapp.data.local.user.UserDao
import com.example.foroapp.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class, PostEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase(){
    //exponer/importar todos los DAO de mis entidades
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao

    companion object {
        //para la instancia de la BD
        @Volatile
        private var INSTANCE: AppDatabase? = null
        //variable para indicar el nombre de la base de datos
        private const val DB_NAME = "foroapp.db"

        //obteniendo la instancia de conexion a la BD
        fun getInstance(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                //construimos la BD
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                //ejecute la creación en caso que sea la primera vez
                    .addCallback(object : RoomDatabase.Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            //corutina insertar datos iniciales en mis tablas
                            CoroutineScope(Dispatchers.IO).launch {
                                val instance = INSTANCE ?: return@launch
                                //precarga de datos por cada tabla que necesitemos
                                val dao = instance.userDao()
                                val seed = listOf(
                                    UserEntity(
                                        name = "Admin",
                                        email = "a@a.cl",
                                        phone = "12345678",
                                        password = "Admin123!"
                                    ),
                                    UserEntity(
                                        name = "Cliente",
                                        email = "c@c.cl",
                                        phone = "12345678",
                                        password = "Cliente123!"
                                    )
                                )
                                //validar que solo se inserten la primera vez
                                if(dao.count() == 0){
                                    seed.forEach { dao.insertar(it) }
                                }
                                //repetimos el proceso para las demas tablas que lo necesiten

                            }

                        }
                    }).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }

}