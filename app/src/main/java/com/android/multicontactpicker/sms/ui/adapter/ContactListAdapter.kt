package com.android.multicontactpicker.sms.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.android.multicontactpicker.sms.R
import com.android.multicontactpicker.sms.model.Contact
import com.android.multicontactpicker.sms.viewmodel.ContactViewModel
import kotlinx.android.synthetic.main.item_contact_list.view.*

class ContactListAdapter(
    private var mList: MutableList<Contact>,
    private val contactViewModel: ContactViewModel
) : RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>(), Filterable {

    private var mFilteredContactList = mutableListOf<Contact>()

    init {
        mFilteredContactList = mList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_contact_list, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact: Contact = mFilteredContactList[position]
        holder.itemView.tvFirstLetter.text = getTextToShowInBubble(position)
        holder.itemView.tvContactName.text =
            if (contact.contactName.isNotEmpty()) contact.contactName else contact.contactNumber
        holder.itemView.tvContactNumber.text = contact.contactNumber
        holder.itemView.ivAdd.setImageResource(
            if (!contact.isSelected) R.drawable.ic_add_circle else R.drawable.ic_remove_circle
        )

        holder.itemView.ivAdd.setOnClickListener {
            if (contact.isSelected) {
                contact.isSelected = false
                contactViewModel.onRemove(contact)
                notifyItemChanged(position)
            } else {
                contact.isSelected = true
                contactViewModel.onAdd(contact)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mFilteredContactList.size
    }

    @SuppressLint("DefaultLocale")
    private fun getTextToShowInBubble(pos: Int): String {
        return mFilteredContactList[pos].contactName[0].toString().toUpperCase()
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getFilter(): Filter {
        return object : Filter() {
            @SuppressLint("DefaultLocale")
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    mFilteredContactList = mList
                    results.values = mFilteredContactList
                    results.count = mFilteredContactList.size
                } else {
                    val resultList = mutableListOf<Contact>()
                    for (row in mList) {
                        if (row.contactName.toLowerCase().contains(charSearch.toLowerCase())) {
                            resultList.add(row)
                        }
                    }
                    results.values = resultList
                    results.count = resultList.size
                }

                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                mFilteredContactList = results?.values as MutableList<Contact>
                notifyDataSetChanged()
            }

        }
    }


}