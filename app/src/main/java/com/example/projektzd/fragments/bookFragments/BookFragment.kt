package com.example.projektzd.fragments.bookFragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.projektzd.GlobalApplication
import com.example.projektzd.R
import com.example.projektzd.database.ApiBookEntity
import com.example.projektzd.database.Book
import com.example.projektzd.database.BookDatabase
import com.example.projektzd.database.DatabaseHelper
import com.example.projektzd.databinding.FragmentBookBinding
import com.example.projektzd.fragments.searchFragments.SearchFragmentViewModel
import com.example.projektzd.fragments.searchFragments.SearchFragmentViewModelFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class BookFragment(
    private val book: ApiBookEntity,
    private val supportFragmentManager: FragmentManager
) :
    Fragment() {

    lateinit var binding: FragmentBookBinding
    lateinit var bookFragmentViewModel: BookFragmentViewModel
    var rentalDateString: String = " "
    var returnDateString: String = " "


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_book, container, false)
        binding.bookTitle.text = book.title
        binding.pageCount.text = book.numberOfPages.toString()
        if (book.authors!!.isNotEmpty())
            binding.author.text = book.authors
        binding.description.text = book.description

        val imgUrl = book.thumbnail?.replace("http://", "https://")

        //TODO: move Glide to utils
        imgUrl?.let {
            val imgUri = imgUrl.toUri().buildUpon()?.build()
            Glide.with(GlobalApplication.appContext!!).load(imgUri)
                .fitCenter()
                .centerCrop()
                .into(binding.bookThumbnail)
        }

        val application = requireNotNull(this.activity).application
        val dataSource = BookDatabase.getInstance(application).bookDatabaseDao
        val viewModelFactory = BookFragmentViewModelFactory(dataSource, supportFragmentManager)
        bookFragmentViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(BookFragmentViewModel::class.java)
        binding.fragmentBookViewModel = bookFragmentViewModel

        binding.addBtn.setOnClickListener {
            bookFragmentViewModel.addBook(book, rentalDateString, returnDateString, activity)
        }

        handlePickDate()
        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    private fun handlePickDate() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val sdf = SimpleDateFormat("dd-MM-yyyy")

        rentalDateString = sdf.format(Date())
        returnDateString = sdf.format(Date())
        binding.rentalDateTxt.text = rentalDateString
        binding.returnDateTxt.text = returnDateString

        handleRentalDate(c, year, month, day, sdf)
        handleReturnDate(c, year, month, day, sdf)
    }

    private fun handleRentalDate(
        c: Calendar,
        year: Int,
        month: Int,
        day: Int,
        sdf: SimpleDateFormat
    ) {
        binding.rentalDateBtn.setOnClickListener {
            activity?.let { it1 ->
                val dpd = DatePickerDialog(
                    it1,
                    DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                        c.set(Calendar.YEAR, mYear)
                        c.set(Calendar.MONTH, mMonth)
                        c.set(Calendar.DAY_OF_MONTH, mDay)

                        rentalDateString = sdf.format(c.time)
                        binding.rentalDateTxt.text = rentalDateString
                    },
                    year,
                    month,
                    day
                )

                dpd.datePicker.maxDate = System.currentTimeMillis()
                dpd.show()
            }
        }
    }


    private fun handleReturnDate(
        c: Calendar,
        year: Int,
        month: Int,
        day: Int,
        sdf: SimpleDateFormat
    ) {
        binding.returnDateBtn.setOnClickListener {
            activity?.let { it1 ->
                val dpd = DatePickerDialog(
                    it1,
                    DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                        c.set(Calendar.YEAR, mYear)
                        c.set(Calendar.MONTH, mMonth)
                        c.set(Calendar.DAY_OF_MONTH, mDay)

                        returnDateString = sdf.format(c.time)
                        binding.returnDateTxt.text = returnDateString
                    },
                    year,
                    month,
                    day
                )

                dpd.datePicker.minDate = System.currentTimeMillis()
                dpd.show()
            }
        }
    }
}