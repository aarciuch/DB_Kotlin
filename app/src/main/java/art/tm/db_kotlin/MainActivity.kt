package art.tm.db_kotlin

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import art.tm.db_kotlin.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.io.BufferedWriter
import java.nio.charset.Charset


@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var base : PersonDB

    private lateinit var recyclerViewAdapter: ArtAdapter

    private lateinit var dao: PersonDao

    private var nazwa : String = "plik.txt"
    private var dane : String = "Ala:Ola:Zosia:"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        base = PersonDB.getPersonDB(application.applicationContext)!!

        dao = base.personDao()

        GlobalScope.launch (Dispatchers.IO) {
            if (dao.getSize() == 0) {
                val initDbContent = getIniDbtContent(R.array.init_db_content)
                for (item in initDbContent) {
                    val index = item.indexOfFirst { it == ':' }
                    val name = item.substring(0,index)
                    val age = item.substring(index+1, item.length)

                    dao.insert(Person(0, name.uppercase(), age.toInt()))

                    Log.i("INDEX", "$index:$name:$age")
                }
            }

            if (dao.getSize() > 0) {
                val b = dao.findByName("ARTUR")
                val c = dao.getSize()

                Log.i("PERSON", b[0].name)
                Log.i("SIZE", c.toString())

                val list = dao.getAll()
                for (item in list) {
                    Log.i("LIST", item.id.toString())
                }
            }
            //dao.clearDB()

            recyclerViewAdapter = ArtAdapter(dao.getAll())
            binding.recyclerList.adapter = recyclerViewAdapter
            recyclerViewAdapter.notifyDataSetChanged()


       }


        binding.bDisplay.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val list = dao.getAll()
                withContext(Dispatchers.Main) {
                    recyclerViewAdapter = ArtAdapter(list)
                    binding.recyclerList.adapter = recyclerViewAdapter
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        }

        binding.bDisplayName.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                //TODO("do background task...")
                val list = dao.findByName(binding.eName.text.toString().uppercase())
                withContext(Dispatchers.Main) {
                    // TODO("Update UI")
                    recyclerViewAdapter = ArtAdapter(list)
                    binding.recyclerList.adapter = recyclerViewAdapter
                }
                //TODO("do background task...")
            }
        }

        binding.bDisplayAge.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val list = dao.findByAge(20)
                withContext(Dispatchers.Main) {
                    recyclerViewAdapter = ArtAdapter(list)
                    binding.recyclerList.adapter = recyclerViewAdapter
                }
            }
        }

        binding.oprecjaButton.setOnClickListener {
            if (binding.operacja.isChecked) {
                val dane1 = odczytZPliku(nazwa)
                Log.i("DANE", dane1)
            } else {
                Log.i("DANE", dane)
                zapisDoPliku(nazwa, dane)
            }
        }
    }

    private fun odczytZPliku(nazwa: String) : String{
        try {
            applicationContext.openFileInput(nazwa).use {
                return it.bufferedReader(Charset.defaultCharset()).readLine()
            }
        } catch (e: Exception) {

        }
    }

    private fun zapisDoPliku(nazwa: String, dane: String) {
       try {
           applicationContext.openFileOutput(nazwa, Context.MODE_PRIVATE).use {
               it.bufferedWriter(Charset.defaultCharset()).write(dane.toString())
           }
       } catch(e: Exception) {

       }
    }


    private fun getIniDbtContent(id: Int): Array<String> {
        return resources.getStringArray(id)
    }
}
