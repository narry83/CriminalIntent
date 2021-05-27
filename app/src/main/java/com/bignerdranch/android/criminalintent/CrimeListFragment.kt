package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "CrimeListFragment"

private const val REGULAR_CRIME_TYPE =0
private const val SERIOUS_CRIME_TYPE =1

class CrimeListFragment : Fragment() {

    private lateinit var crimeRecylerView: RecyclerView
    private var adapter: CrimeAdapter? = null

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes: ${crimeListViewModel.crimes.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecylerView= view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecylerView.layoutManager = LinearLayoutManager(context)

        updateUI()

        return view
    }

    private fun updateUI() {
        val crimes = crimeListViewModel.crimes
        adapter = CrimeAdapter(crimes)
        crimeRecylerView.adapter = adapter
    }

    private abstract inner class AbstractCrimeHolder(view: View):
        RecyclerView.ViewHolder(view){

            abstract fun bind(crime: Crime)
    }

    private inner class CrimeHolder(view: View)
        :AbstractCrimeHolder(view), View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        override fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            solvedImageView.visibility = if(crime.isSolved){
                View.VISIBLE
            }else{
                View.GONE
            }
        }

        override fun onClick(v: View?) {
            Toast.makeText(context,"${crime.title} pressed!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private inner class SeriousCrimeHolder(view: View)
        :AbstractCrimeHolder(view){

        private lateinit var crime: Crime

        private val contactPoliceButton: Button = itemView.findViewById(R.id.contact_police_button)

        init {
            contactPoliceButton.setOnClickListener {
                val callingMessage= getString(R.string.contact_police_message, crime.title)
                Toast.makeText(context,callingMessage, Toast.LENGTH_SHORT)
                .show() }
        }

        override fun bind(crime: Crime){
            this.crime = crime
        }
        }


    private inner class CrimeAdapter(var crimes: List <Crime>)
        :RecyclerView.Adapter<AbstractCrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractCrimeHolder {

            if (viewType == SERIOUS_CRIME_TYPE) {
                val view = layoutInflater.inflate(R.layout.list_item_serious_crime, parent, false)
                return SeriousCrimeHolder(view)
            } else {
                val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                return CrimeHolder(view)
            }
        }

        override fun onBindViewHolder(holder: AbstractCrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

        override fun getItemViewType(position: Int)
            =if (crimes[position].requiresPolice){SERIOUS_CRIME_TYPE} else {REGULAR_CRIME_TYPE}

        override fun getItemCount() = crimes.size


    }

    companion object{
        fun newInstance(): CrimeListFragment{
            return CrimeListFragment()
        }
    }
}