package art.tm.db_kotlin

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import art.tm.db_kotlin.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.Scanner


@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var base : PersonDB

    private lateinit var recyclerViewAdapter: ArtAdapter

    private lateinit var dao: PersonDao

    private var nazwa : String = "plik.txt"
    private var dane : ArrayList<String> = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        base = PersonDB.getPersonDB(application.applicationContext)!!

        dao = base.personDao()

        dane.add("Ula")
        dane.add("Ola")
        dane.add("Ela")
        dane.add("Ala")

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
                val dane1 = odczytZPliku(nazwa, 0)
                Log.i("DANE", "dane tryb 0: ${dane1}")
                val dane2 = odczytZPliku(nazwa, 1)
                Log.i("DANE", "dane tryb 1: ${dane2}")
            } else {
                Log.i("DANE", "${dane}")
                zapisDoPliku(nazwa, dane)
            }
        }
    }

    private fun odczytZPliku(nazwa: String, tryb: Int): ArrayList<String> = runBlocking {
        var wynik = arrayListOf<String>()
        var linia: String
        if (tryb == 0) {
            val jobTryb0 = async {
                try {
                    applicationContext.openFileInput(nazwa).use {
                        var bufReader = BufferedReader(it.reader())
                        do {
                            linia = bufReader.readLine()
                            if (linia != null) wynik.add(linia)
                        } while (linia != null)
                    }
                } catch (e: Exception) {
                    e.message?.let { Log.i("DANE", it) }
                }
            }
            jobTryb0.await()
            //    Log.i("DANE", "${wynik}")
        } else if (tryb == 1) {
            val jobTryb1 = async {
                try {
                    val sc = Scanner(openFileInput(nazwa))
                    while(sc.hasNextLine()) {
                        wynik.add(sc.nextLine())
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            jobTryb1.await()
        }
        return@runBlocking wynik
    }

    private fun zapisDoPliku(nazwa: String, dane: ArrayList<String>) {
        GlobalScope.launch {
            try {
                applicationContext.openFileOutput(nazwa, Context.MODE_PRIVATE).use {
                    for (item in dane) {
                        it.write(item.toByteArray(Charset.defaultCharset()))
                        it.write("\n".toByteArray(Charset.defaultCharset()))
                    }
                }
            } catch (e: Exception) {

            }
        }
    }


    private fun getIniDbtContent(id: Int): Array<String> {
        return resources.getStringArray(id)
    }
}
