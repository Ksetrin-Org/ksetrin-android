package org.ksetrin.ksetrin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.NumberPicker
import org.ksetrin.ksetrin.R

class ProfileFragment : Fragment() {

    private lateinit var numberPicker: NumberPicker

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        numberPicker = requireActivity().findViewById(R.id.profileFragmentNumberPicker)
        numberPicker.maxValue = 100
        numberPicker.minValue = 14
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
}