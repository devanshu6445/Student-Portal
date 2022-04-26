package com.college.portal.studentportal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.college.portal.studentportal.R
import com.college.portal.studentportal.extensionFunctions.*
import com.college.portal.studentportal.roomDatabase.groupEverything.Participant
import com.college.portal.studentportal.ui.group_participant_details.GroupDetailsRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class GroupParticipantsAdapter(
    private val context: Context,
    private val participantList: MutableList<Participant>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val participantRepo = GroupDetailsRepository.getRepository()
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())
    private lateinit var loggedInParticipant: Participant

    /*companion object {
        private const val TAG = "GroupParticipantAdapter: "
    }*/

    init {
        ioScope.launch {
            participantRepo?.getLoggedInParticipant()?.collect {
                if (it != null) {
                    loggedInParticipant = it
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val participantView =
            LayoutInflater.from(parent.context).inflate(R.layout.groups_item, parent, false)
        return ParticipantViewHolder(participantView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val participant = participantList[position]
        val participantViewHolder = holder as ParticipantViewHolder
        participantViewHolder.apply {
            groupLastPostTime.visibility = View.GONE
            groupLastPost.visibility = View.GONE
            participantName.text = participant.name
            participantName.gravity = Gravity.CENTER_VERTICAL
        }

        when (participant.role) {
            "admin" -> participantViewHolder.participantRole.text =
                context.getString(R.string.admin)
            "moderator" -> participantViewHolder.participantRole.text =
                context.getString(R.string.moderator)
            "teacher" -> participantViewHolder.participantRole.text =
                context.getString(R.string.teacher)
            "student" -> participantViewHolder.participantRole.visibility = View.GONE

            else -> participantViewHolder.participantRole.visibility = View.VISIBLE
        }

        Glide.with(participantViewHolder.profileImage.context)
            .load(participant.imageURL)
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(participantViewHolder.profileImage)

        participantViewHolder.root.setOnClickListener {
            when (val role = loggedInParticipant.role) {
                "admin", "teacher" -> {
                    val items = when {
                        role == "admin" && participant.role != "admin" -> teacherMenuForParticipant
                        role == "teacher" && (participant.role == "teacher" || participant.role == "admin") -> studentMenuForParticipant
                        else -> teacherMenuForParticipant
                    }
                    MaterialAlertDialogBuilder(
                        participantViewHolder.itemView.context,
                        R.style.ThemeOverlay_App_MaterialAlertDialog
                    )
                        .setItems(items) { _, which ->
                            when (which) {
                                0 -> {
                                    Toast.makeText(context, "coming soon..", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                1 -> {
                                    banStudent(
                                        participantViewHolder.itemView.context,
                                        participant.uid
                                    )
                                }
                                2 -> {
                                    ioScope.launch {
                                        val result = participantRepo?.promoteUser(participant)
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, result, Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                                }
                                3 -> {
                                    ioScope.launch {
                                        val result = participantRepo?.demoteUser(participant)
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, result, Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                                }
                            }
                        }
                        .show()
                }
                "moderator" -> {
                    val items: Array<String> = if (participant.role != "student")
                        studentMenuForParticipant
                    else
                        moderatorMenuForParticipant
                    MaterialAlertDialogBuilder(
                        participantViewHolder.itemView.context,
                        R.style.ThemeOverlay_App_MaterialAlertDialog
                    )
                        .setItems(items) { _, which ->
                            when (which) {
                                0 -> {
                                    Toast.makeText(context, "Coming soon..", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                1 -> {
                                    banStudent(
                                        participantViewHolder.itemView.context,
                                        participant.uid
                                    )
                                }
                            }
                        }
                        .show()
                }
                "student" -> {
                    MaterialAlertDialogBuilder(
                        participantViewHolder.itemView.context,
                        R.style.ThemeOverlay_App_MaterialAlertDialog
                    )
                        .setItems(studentMenuForParticipant) { _, which ->
                            when (which) {
                                0 -> {
                                    Toast.makeText(context, "Coming soon..", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                        .show()
                }
                else -> {

                }
            }
        }

    }

    private fun banStudent(context: Context, bannedUID: String) {
        var banTime = internalBanValues[0]
        MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setPositiveButton(
                "Ban"
            ) { dialog, _ ->
                ioScope.launch {
                    val result = if (banTime == -1L) participantRepo?.banStudent(
                        banTime,
                        bannedUID,
                        false
                    ) else participantRepo?.banStudent(banTime, bannedUID, true)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@GroupParticipantsAdapter.context,
                            result,
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setSingleChoiceItems(banTimes, 0) { _, which ->
                banTime = internalBanValues[which]
            }
            .show()
    }

    override fun getItemCount(): Int = participantList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateParticipantList(participantList: List<Participant>) {
        this.participantList.clear()
        this.participantList.addAll(participantList)
        notifyDataSetChanged()
    }

    class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val profileImage: ImageView = itemView.findViewById(R.id.group_image)
        val participantName: TextView = itemView.findViewById(R.id.group_name)
        val groupLastPost: TextView = itemView.findViewById(R.id.lastPost)
        val participantRole: TextView = itemView.findViewById(R.id.groupPurpose)
        val groupLastPostTime: TextView = itemView.findViewById(R.id.lastPostTime)
        val root: View = itemView.rootView
    }
}