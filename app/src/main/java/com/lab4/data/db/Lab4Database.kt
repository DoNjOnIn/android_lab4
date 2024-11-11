package com.lab4.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lab4.data.dao.SubjectDao
import com.lab4.data.dao.SubjectLabsDao
import com.lab4.data.entity.SubjectEntity
import com.lab4.data.entity.SubjectLabEntity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Lab4Database - the main database class
 * - extends on RoomDatabase()
 * - marked with @Database annotation for generating communication interfaces
 * - in annotation are added all your entities (tables)
 * - includes abstract properties of all DAO interfaces for each entity (table)
 */
@Database(entities = [SubjectEntity::class, SubjectLabEntity::class], version = 1)
abstract class Lab4Database : RoomDatabase() {
    //DAO properties for each entity (table)
    // must be abstract (because Room will generate instances by itself)
    abstract val subjectsDao: SubjectDao
    abstract val subjectLabsDao: SubjectLabsDao
}

/**
 * DatabaseStorage - custom class where you initialize and store Lab4Database single instance
 *
 */
object DatabaseStorage {
    // ! Important - all operations with DB must be done from non-UI thread!
    // coroutineScope: CoroutineScope - is the scope which allows to run asynchronous operations
    // > we will learn it soon! For now just put it here
    private val coroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        },
    )

    // single instance of Lab4Database
    private var _database: Lab4Database? = null

    /**
        Function of initializing and getting Lab4Database instance
        - is invoked from place where DB should be used (from Compose screens)
        [context] - context from Compose screen to init DB
    */
    fun getDatabase(context: Context): Lab4Database {
        if (_database != null) return _database as Lab4Database

        _database = Room.databaseBuilder(
            context.applicationContext,
            Lab4Database::class.java,
            "lab4Database"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Run preloading only when the database is created for the first time
                    preloadData()
                }
            })
            .build()

        return _database as Lab4Database
    }

    /**
        Function for preloading some initial data to DB
     */
    private fun preloadData() {
        // List of subjects
        val listOfSubject = listOf(
            SubjectEntity(id = 1, title = "Мережева безпека"),
            SubjectEntity(id = 2, title = "Subject 2"),
            SubjectEntity(id = 3, title = "Subject 3"),
        )
        // List of labs
        val listOfSubjectLabs = listOf(
            SubjectLabEntity(
                id = 1,
                subjectId = 1,
                title = "Налаштування механізмів безпеки комутаторів ETHERNET",
                description = "Дослідити принципи налаштування механізмів безпеки на комутаторах Cisco Catalyst",
                comment = "ноу",
                isCompleted = true,
            ),
            SubjectLabEntity(
                id = 2,
                subjectId = 1,
                title = "ОНОВЛЕННЯ IOS, РЕЗЕРВНЕ КОПІЮВАННЯ \n" +
                        "ТА ВІДНОВЛЕННЯ НАЛАШТУВАНЬ КОМУТАТОРА \n" +
                        "З ВИКОРИСТАННЯМ ПРОТОКОЛУ TFTP\n",
                description = "Оновлення IOS, резервне копіювання та відновлення налаштувань комутатора з використанням протоколу TFTP",
                comment = "",
                inProgress = true,
            ),
            SubjectLabEntity(
                id = 3,
                subjectId = 1,
                title = "ВИКОРИСТАННЯ СПИСКІВ КОНТРОЛЮ ДОСТУПУ ACL ДЛЯ УПРАВЛІННЯ МЕРЕЖЕВИМ ТРАФІКОМ\n",
                description = "Використання списків контролю доступу ACL для управління мережевим трафіком",
                comment = "",
                inProgress = true,
            ),
            SubjectLabEntity(
                id = 4,
                subjectId = 1,
                title = "ОВІДНОВЛЕННЯ ПАРОЛІВ НА КОМУТАТОРАХ \n" +
                        "ТА  МАРШРУТИЗАТОРАХ Cisco",
                description = "Відновлення паролів на комутаторах та маршрутизаторах Cisco.",
                comment = "",
                inProgress = true,
            ),
            SubjectLabEntity(
                id = 5,
                subjectId = 1,
                title = "Конфігурація і перевірка IPsec VPN між двома пунктами (site-to-site) за допомогою інтерфейсу командного рядка",
                description = "Конфігурація і перевірка IPsec VPN між двома пунктами (site-to-site) за допомогою інтерфейсу командного рядка.",
                comment = "",
                inProgress = true,
            ),
            SubjectLabEntity(
                id = 6,
                subjectId = 1,
                title = "Забезпечення безпеки на 2-му рівні",
                description = "Налаштування безпеку на 2 рівні моделі OSI.",
                comment = "",
                inProgress = true,
            ),

            SubjectLabEntity(
                id = 1,
                subjectId = 2,
                title = "еееее",
                description = "ккккк",
                comment = "ноу",
                isCompleted = true,
            ),
        )

        // Request to add all Subjects from the list to DB
        listOfSubject.forEach { subject ->
            // coroutineScope.launch{...} - start small thread where you can make query to DB
            coroutineScope.launch {
                // INSERT query to add Subject (subjectsDao is used)
                _database?.subjectsDao?.addSubject(subject)
            }
        }
        // Request to add all Labs from the list to DB
        listOfSubjectLabs.forEach { lab ->
            coroutineScope.launch {
                // INSERT query to add Lab (subjectLabsDao is used)
                _database?.subjectLabsDao?.addSubjectLab(lab)
            }
        }
    }
}