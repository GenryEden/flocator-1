package com.example.flocator.settings

import android.annotation.SuppressLint
import android.database.Observable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.flocator.R
import com.example.flocator.common.repository.MainRepository
import com.example.flocator.common.storage.store.user.info.UserInfo
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Emitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.LinkedList
import java.util.concurrent.Flow.Publisher
import javax.inject.Inject

class FriendListAdapter (
    private val mainRepository: MainRepository
): RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {
    private val compositeDisposable = CompositeDisposable()
    val publisher: PublishSubject<Friend> = PublishSubject.create()
    private var friends: LinkedList<Friend> = LinkedList()
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val icon: ImageView = itemView.findViewById(R.id.friend_avatar)
        private val name: TextView = itemView.findViewById(R.id.friend_name)
        private val tick: ImageView = itemView.findViewById(R.id.friend_tick)
        private val friendElement: ConstraintLayout = itemView.findViewById(R.id.friend_element)
        fun bind(friend: Friend) {
            name.text = friend.name
            if (friend.isChecked) {
                tick.imageAlpha = 100
            } else {
                tick.imageAlpha = 0
            }
            friendElement.setOnClickListener {
                friend.isChecked = !friend.isChecked
                publisher.onNext(friend)
                if (friend.isChecked) {
                    tick.imageAlpha = 100
                } else {
                    tick.imageAlpha = 0
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_privacy_friend, null, false)
        )
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    fun add(friend: Friend) {
        friends.add(friend)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(friends[position])
    }

    fun selectAll() {
        for ((i, friend) in friends.withIndex()) {
            if (!friend.isChecked) {
                friend.isChecked = true
                notifyItemChanged(i)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newFriends: List<Friend>) {
        friends = LinkedList(newFriends.toList())
        notifyDataSetChanged()
    }
    fun unselectAll() {
        for ((i, friend) in friends.withIndex()) {
            if (friend.isChecked) {
                friend.isChecked = false
                notifyItemChanged(i)
            }
        }
    }

    fun all(filter: (friend: Friend) -> Boolean): Boolean {
        return friends.all(filter)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        compositeDisposable.dispose()
        super.onViewDetachedFromWindow(holder)
    }
}