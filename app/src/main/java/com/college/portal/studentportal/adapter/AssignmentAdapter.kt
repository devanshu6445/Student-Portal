package com.college.portal.studentportal.adapter

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.view.*
import android.webkit.URLUtil
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.college.portal.studentportal.R
import com.college.portal.studentportal.WrapContentLinearLayoutManager
import com.college.portal.studentportal.roomDatabase.assignment.Assignment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.File

class AssignmentAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), RecyclerViewAdapterUpdate<Assignment> {

    private val assignmentList = mutableListOf<Assignment>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.assignment_layout_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        val assignment = assignmentList[position]
        viewHolder.apply {
            assignmentName.text = assignment.assignmentTitle
            assignmentDeadline.text = assignment.assignmentDeadline
            assignmentInstruction.text = assignment.assignmentInstruction
            assignmentSubject.text = assignment.assignmentSubjectCode
        }
        if(assignment.assignmentURL!= null){
            viewHolder.attachment.setOnClickListener {
                showMediaDownloadDialog(assignment.assignmentURL!!)
            }
        }else{
            viewHolder.attachment.apply {
                isEnabled = false
                alpha = 0.5f
            }
        }
    }

    private fun showMediaDownloadDialog(url:String) {

        val dialog = MaterialAlertDialogBuilder(context,R.style.ThemeOverlay_App_MaterialAlertDialog).setView(R.layout.assignment_media_layout).show()

        val mediaRV = dialog.findViewById<RecyclerView>(R.id.assignment_mediaRV)
        mediaRV?.layoutManager = WrapContentLinearLayoutManager(context)
        mediaRV?.adapter = AssignmentMediaDownloadAdapter(url,context)
    }

    override fun getItemCount(): Int {
        return assignmentList.size
    }

    override fun updateList(updatedList: List<Assignment>) {
        assignmentList.clear()
        assignmentList.addAll(updatedList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var attachment: ImageButton = itemView.findViewById(R.id.assignmentAttachments)
        var assignmentName: TextView = itemView.findViewById(R.id.assignmentTitle)
        var assignmentInstruction: TextView = itemView.findViewById(R.id.assignmentInstructions)
        var assignmentDeadline: TextView = itemView.findViewById(R.id.assignmentDeadLine)
        var assignmentSubject: TextView = itemView.findViewById(R.id.assignmentsSubjectCode)
    }
}

class AssignmentMediaDownloadAdapter(jsonUrl:String,private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var uriList: List<String>
    private val mStorage = FirebaseStorage.getInstance()
    init {
        val urlList = mutableListOf<String>()
        val jsonArray = JSONArray(jsonUrl)
        for(url in 0 until jsonArray.length()){
            urlList.add(jsonArray.get(url) as String)
        }
        uriList = urlList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.media_layout_item,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        val url = uriList[position]
        val filename = mStorage.getReferenceFromUrl(url).name

        viewHolder.fileName.text = filename
        viewHolder.fileDownload.setOnClickListener {
            checkPermission(url)
        }
    }

    private fun checkPermission(url: String){
        if(context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(context, "Please provide permission for storage.", Toast.LENGTH_SHORT).show()
        }else{
            downloadMedia(url)
        }
    }

    private fun downloadMedia(url: String){
        val reference  = mStorage.getReferenceFromUrl(url)
        val extension = URLUtil.guessFileName(url,null,null)

        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath+"/"+reference.name)

        kotlin.runCatching {
            file.createNewFile()
        }

        reference.getFile(Uri.fromFile(file)).addOnSuccessListener {
            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context, "Error ${it.message} occurred", Toast.LENGTH_SHORT).show()
        }.addOnPausedListener {
            Toast.makeText(context, "File download has been paused.", Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int {
        return uriList.size
    }
    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val fileName:TextView = itemView.findViewById(R.id.fileName)
        val fileDownload:ImageButton = itemView.findViewById(R.id.fileDownload)
    }
}