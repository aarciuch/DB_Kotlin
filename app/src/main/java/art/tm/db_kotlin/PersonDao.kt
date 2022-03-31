package art.tm.db_kotlin

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PersonDao  {

    @Query("SELECT * FROM person_table")
    fun getAll() : List<Person>

    //@Query("SELECT * FROM person_table WHERE name_column LIKE :name LIMIT 10")
    @Query("SELECT * FROM person_table WHERE name_column LIKE :name LIMIT 10")
    fun findByName(name: String): List<Person>

    @Query("SELECT * FROM person_table WHERE age_column > :age ")
    fun findByAge(age: Int): List<Person>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(person: Person)

    @Query("DELETE FROM person_table")
    fun clearDB()

    @Query("SELECT COUNT(*) FROM person_table")
    fun getSize(): Int
}