package com.college.portal.studentportal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AnnouncementInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnnouncementInfoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var  requestQueue:RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_announcement_info, container, false)
        view.findViewById<Button>(R.id.fetch_test).visibility = View.GONE
        val w = view.findViewById<WebView>(R.id.webVV)
        w.loadUrl("file:///android_asset/term_condition.html")
        w.settings.javaScriptEnabled
        return view
    }
    private fun getDataFromURL(){

        val stringRequest = StringRequest(
            Request.Method.GET,
            URL, { response ->
                Log.d(TAG, "getDataFromURL1: $response")
                 },
            {
                Log.e(TAG, "getDataFromURL: ${it.networkResponse}", )
            }
        )

        requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(stringRequest)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AnnouncementInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val URL ="https://my-json-server.typicode.com/easygautam/data/users"
        private const val TAG = "AnnouncementInfoFragment: "
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnnouncementInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}