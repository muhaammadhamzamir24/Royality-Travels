package com.myapp.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        val data=intent.getSerializableExtra("trips") as Trips

        val location=findViewById<TextView>(R.id.lname)
        val price=findViewById<TextView>(R.id.lprice)
        val seats=findViewById<TextView>(R.id.lseats)
        val start=findViewById<TextView>(R.id.lstart)
        val end=findViewById<TextView>(R.id.lend)
        val dis=findViewById<TextView>(R.id.ldiscount)

        val btn=findViewById<Button>(R.id.tripBook)
        val seatsentered=findViewById<AppCompatEditText>(R.id.Seats)

        location.text=location.text.toString().plus( data.location)
        price.text=price.text.toString().plus(data.price.toString())
        seats.text=seats.text.toString().plus(data.seats.toString())
        start.text=start.text.toString().plus(data.starttripDate)
        end.text=end.text.toString().plus(data.endtripDate)
        dis.text=dis.text.toString().plus(data.discount)

        btn.setOnClickListener {

            val seatsEntered=seatsentered.text.toString().toInt()

            if (seatsEntered<0 || seatsEntered>data.seats)
                return@setOnClickListener
            val insertion=FirebaseHandler<UserHistory>("History")
            val tempData=data
            tempData.seats=seatsEntered
            val dataToInsert=UserHistory("abdullah@gmail",tempData)
            insertion.insert(dataToInsert)
            UpdateSeats("Trips",data,seatsEntered)

        }
    }
    fun UpdateSeats(tableName:String,details:Trips,bookSeats:Int)
    {
        val reference=FirebaseDatabase.getInstance()
        reference.getReference(tableName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(d in snapshot.children)
                {
                    val answer=d.getValue(Trips::class.java)
                    if(answer!!.id==details.id)
                    {
                        Toast.makeText(applicationContext,"In",Toast.LENGTH_SHORT).show()
                        val temp=answer.seats-bookSeats
                        answer.seats=temp
                        d.ref.setValue(answer)
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}