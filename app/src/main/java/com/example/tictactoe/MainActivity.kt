package com.example.tictactoe

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var  mFirebaseAnalytics:FirebaseAnalytics? = null
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    private var myEmail:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        var b:Bundle = intent.extras
        myEmail = b.getString("email")
        IncomingCalls()
    }

    fun BuClick(view:View) {
        val buSelected = view as Button
        var cellID = 0

        when(buSelected.id) {
            R.id.bu1 -> cellID = 1
            R.id.bu2 -> cellID = 2
            R.id.bu3 -> cellID = 3
            R.id.bu4 -> cellID = 4
            R.id.bu5 -> cellID = 5
            R.id.bu6 -> cellID = 6
            R.id.bu7 -> cellID = 7
            R.id.bu8 -> cellID = 8
            R.id.bu9 -> cellID = 9
        }
        myRef.child("PlayerOnline").child(sessionID!!).child(cellID.toString()).setValue(myEmail)
    }

    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()
    var activePlayer = 1

    fun PlayGame (cellID:Int, buSelected:Button) {

        if(activePlayer == 1) {
            buSelected.text = "X"
            buSelected.setBackgroundResource(R.color.greyGreen)
            player1.add(cellID)
            activePlayer = 2



        } else {
            buSelected.text = "O"
            buSelected.setBackgroundResource(R.color.yellow)
            player2.add(cellID)
            activePlayer = 1


        }
        buSelected.isEnabled = false
        CheckWinner()



    }

    fun CheckWinner () {
        var winner = -1
        //row1
        if(player1.contains(1) && player1.contains(2) && player1.contains(3)) {
            winner = 1
        }
        if(player2.contains(1) && player2.contains(2) && player2.contains(3)) {
            winner = 2
        }
        //row2
        if(player1.contains(4) && player1.contains(5) && player1.contains(6)) {
            winner = 1
        }
        if(player2.contains(4) && player2.contains(5) && player2.contains(6)) {
            winner = 2
        }
        //row3
        if(player1.contains(7) && player1.contains(8) && player1.contains(9)) {
            winner = 1
        }
        if(player2.contains(7) && player2.contains(8) && player2.contains(9)) {
            winner = 2
        }
        //column1
        if(player1.contains(1) && player1.contains(4) && player1.contains(7)) {
            winner = 1
        }
        if(player2.contains(1) && player2.contains(4) && player2.contains(7)) {
            winner = 2
        }
        //column2
        if(player1.contains(2) && player1.contains(5) && player1.contains(8)) {
            winner = 1
        }
        if(player2.contains(2) && player2.contains(5) && player2.contains(8)) {
            winner = 2
        }
        //column3
        if(player1.contains(3) && player1.contains(6) && player1.contains(9)) {
            winner = 1
        }
        if(player2.contains(3) && player2.contains(6) && player2.contains(9)) {
            winner = 2
        }

        //diagon159
        if(player1.contains(1) && player1.contains(5) && player1.contains(9)) {
            winner = 1
        }
        if(player2.contains(1) && player2.contains(5) && player2.contains(9)) {
            winner = 2
        }
        //diagon357
        if(player1.contains(3) && player1.contains(5) && player1.contains(7)) {
            winner = 1
        }
        if(player2.contains(3) && player2.contains(5) && player2.contains(7)) {
            winner = 2
        }

        if(winner != -1) {

            if(winner == 1) {
                Toast.makeText(this, "Player X, win the game!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Player O, win the game!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun AutoPlay (cellID: Int) {


        var buSelected:Button?
        when (cellID) {
            1-> buSelected = bu1
            2-> buSelected = bu2
            3-> buSelected = bu3
            4-> buSelected = bu4
            5-> buSelected = bu5
            6-> buSelected = bu6
            7-> buSelected = bu7
            8-> buSelected = bu8
            9-> buSelected = bu9
            else -> {
                buSelected = bu1
            }

        }
        PlayGame(cellID, buSelected)
    }

    fun RequestEvent(view: View) {
        var userEmail = email_player.text.toString()
        myRef.child("Users").child(SplitString(userEmail)).child("Request").push().setValue(myEmail)
        PlayerOnline(SplitString(myEmail!!) + SplitString(userEmail))
        request_btn.setBackgroundResource(R.color.request)
        Toast.makeText(applicationContext, "You sent the request!", Toast.LENGTH_SHORT).show()
        playerSymbol = "X"
    }

    fun AcceptEvent(view: View) {
        var userEmail = email_player.text.toString()
        myRef.child("Users").child(SplitString(userEmail)).child("Request").push().setValue(myEmail)
        PlayerOnline( SplitString(userEmail) + SplitString(myEmail!!))
        accept_btn.setBackgroundResource(R.color.accept)
        Toast.makeText(applicationContext, "You accepted the request!", Toast.LENGTH_SHORT).show()
        playerSymbol = "O"
    }

    var sessionID:String? = null
    var playerSymbol:String? = null


    fun PlayerOnline(sessionID:String) {
        this.sessionID = sessionID
        myRef.child("PlayerOnline").removeValue()
        myRef.child("PlayerOnline").child(sessionID)
            .addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {
                    Log.d("VINARIJA", "sta vrati FirebaseTry=" + dataSnapshot.toString())
                    player1.clear()
                    player2.clear()
                    val td=dataSnapshot!!.children
                    if(td!=null){
                        Log.d("VINARIJA", "Lista poteza=" + td)

                        td.forEach {

                            if(it.value == myEmail) {
                                activePlayer =  if (playerSymbol === "X") 1 else 2

                            } else {
                                activePlayer =  if (playerSymbol === "X") 2 else 1

                            }
                            AutoPlay(it.key?.toInt()!!)
                        }

                    }

                } catch (ex:Exception) {
                    Log.d("VINARIJA", "sta vrati FirebaseCatch=" + ex.toString())
                }

            }

        })

    }

    var number = 0
    fun IncomingCalls() {
        myRef.child("Users").child(SplitString(myEmail!!))
            .child("Request").addValueEventListener(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    try {
                        val td = dataSnapshot.children
                        if(td!=null){
                            var value:String
                            td.forEach {
                                value = it.value.toString()
                                email_player.setText(value)
                                val notifyMe = Notifications()
                                notifyMe.Notify(applicationContext, value + "wants to play Tic tac toe", number)
                                number++
                                Log.d("VINARIJA", "notify=" + notifyMe.toString())
                                Log.d("VINARIJA", "number=" + number.toString())
                                Log.d("VINARIJA", "value=" + value)
                                myRef.child("Users").child(SplitString(myEmail!!)).child("Request").setValue(true)


                            }
                        }

                    } catch (ex:Exception) {

                    }

                }

            })
    }

    fun SplitString (str:String): String {
        var split = str.split("@")
        return split[0]

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("TicTacToeGame", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
