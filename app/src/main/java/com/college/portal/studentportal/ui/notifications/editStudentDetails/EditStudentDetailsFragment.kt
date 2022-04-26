package com.college.portal.studentportal.ui.notifications.editStudentDetails

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.college.portal.studentportal.R

class EditStudentDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = EditStudentDetailsFragment()
    }

    private lateinit var viewModel: EditStudentDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_student_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditStudentDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}