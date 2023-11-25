package com.example.greeterblockchain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.web3j.abi.FunctionReturnDecoder
import java.util.concurrent.Future
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.infura.InfuraHttpService
import org.web3j.tx.ClientTransactionManager
import org.web3j.tx.RawTransactionManager
import java.math.BigInteger


class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private lateinit var fab: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab = findViewById(R.id.fab)
        var greetTextview = findViewById<TextView>(R.id.textView)
        var greetingValueBtn = findViewById<TextView>(R.id.greetingValueBtn)
        var changeGreetEditText = findViewById<TextView>(R.id.editText)
        var changeGreetingBtn = findViewById<TextView>(R.id.changeGreetingBtn)

//        val chainId = 80001
        val contractAddress = "0x7b493c1D299650FDf3127DaF2188B24CA797e012"
        val url = "https://sepolia.infura.io/v3/2ba7c114e14144bbb6d6e827d3547fe3"
        val web3j = Web3jFactory.build(InfuraHttpService(url))
//        val gasLimit: BigInteger = BigInteger.valueOf(20_000_000_000L)
//        val gasPrice: BigInteger = BigInteger.valueOf(43000)
        val gasLimit: BigInteger = BigInteger.valueOf(20_000_000_000L) // Adjust gas limit here
        val gasPrice: BigInteger = BigInteger.valueOf(4300000) // Adjust gas price here

        val credentials =
            Credentials.create("7f669b0ff2156a7e5ee7c48ab6f9fe14017d38396a641afef11fede9b06069a5")
//        val txManager = RawTransactionManager(web3j, credentials, chainId.toByte())

        val greeter =
            Greeter_sol_Greeter.load(contractAddress, web3j, credentials, gasLimit, gasPrice)

        fab.setOnClickListener {
            val thread = Thread(Runnable {
                try {
                    val isValid = !greeter.isValid
                    runOnUiThread {
                        showToast("Contract is valid: $isValid")
                    }

//                    val greeting: Future<TransactionReceipt>? = greeter.greet().sendAsync()
//                    val receipt: TransactionReceipt? = greeting?.get()
////                    val greetingValue = greeter.processGreetResponse(receipt)
//                    runOnUiThread {
////                        showToast("Greeting value returned: $receipt")
////                    }
//                        greetTextview.text = "Greeting value returned: $receipt"
//                    }

                    // read from contract
//                    val greeting: Future<String>? = greeter.greet().sendAsync()
//                    val convertToString: String? = greeting?.get()
//                    Log.d(TAG, "greeting value returned: $convertToString")
//
//                    runOnUiThread {
//                        greetTextview.text = "Greeting value returned: $convertToString"
//                    }
//
//                    val transactionReceipt: Future<TransactionReceipt>? =
//                        greeter.changeGreeting("Greeting changed from an Android App").sendAsync()
//                    val result =
//                        "Successful transaction. Gas used: ${transactionReceipt?.get()?.blockNumber}  ${transactionReceipt?.get()?.gasUsed}"
//                    runOnUiThread {
//                        showToast(result)
//                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        showToast("Error accessing contract: ${e.message}")
                    }
                }
            })

            thread.start()
        }
        greetingValueBtn.setOnClickListener {
            val thread = Thread(Runnable {
                try {
                    // read from contract
                    val greeting: Future<String>? = greeter.greet().sendAsync()
                    val convertToString: String? = greeting?.get()
//                    Log.d(TAG, "greeting value returned: $convertToString")

                    runOnUiThread {
                        greetTextview.text = "Greeting value returned: $convertToString"
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        showToast("Error accessing contract: ${e.message}")
                    }
                }
            })

            thread.start()
        }
        changeGreetingBtn.setOnClickListener {
            val thread = Thread(Runnable {
                try {
                    var greeting = changeGreetEditText.text.toString()
                    if(greeting.isNotEmpty()) {
                        val transactionReceipt: Future<TransactionReceipt>? =
                            greeter.changeGreeting(greeting).sendAsync()
                        val result =
                            "Successful transaction. Gas used: ${transactionReceipt?.get()?.gasUsed} Block Number: ${transactionReceipt?.get()?.blockNumber}"
                        runOnUiThread {
                            showToast(result)
                        }
                    }
                    else{
                        showToast("Cannot have an empty greeting Value")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        showToast("Error accessing contract: ${e.message}")
                    }
                }
            })

            thread.start()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}