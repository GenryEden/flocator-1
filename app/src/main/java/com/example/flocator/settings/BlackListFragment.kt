package com.example.flocator.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flocator.R
import com.example.flocator.common.repository.MainRepository
import com.example.flocator.settings.FriendViewUtilities.getNumOfColumns
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@AndroidEntryPoint
class BlackListFragment : Fragment(), SettingsSection {
    private lateinit var friendListAdapter: FriendListAdapter
    @Inject lateinit var mainRepository: MainRepository
    private val compositeDisposable = CompositeDisposable()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_black_list, container, false)
        val recyclerView = fragmentView.findViewById<RecyclerView>(R.id.blacklist_recycler_view)
        val backButton = fragmentView.findViewById<FrameLayout>(R.id.blacklist_back_button)
        val unselectAllButton = fragmentView.findViewById<FrameLayout>(R.id.blacklist_unselect_all_frame)

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        friendListAdapter = FriendListAdapter(mainRepository)
        recyclerView.adapter = friendListAdapter
        recyclerView.layoutManager = GridLayoutManager(context, getNumOfColumns(context, 120.0f))

        unselectAllButton.setOnClickListener {
            if (friendListAdapter.all { friend -> !friend.isChecked }) {
                friendListAdapter.selectAll()
            } else {
                friendListAdapter.unselectAll()
            }
        }

        compositeDisposable.add(mainRepository.restApi.getBlockedByCurrentUser()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { userInfoList ->
                    userInfoList.map {
                        userInfo ->
                        {
                            if (userInfo.avatarUri != null) {
                                compositeDisposable.add(
                                mainRepository.photoLoader.getPhoto(userInfo.avatarUri)
                                    .observeOn(Schedulers.io())
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                        {
                                            activity?.runOnUiThread {
                                                friendListAdapter.add(
                                                    Friend(
                                                        userInfo.userId,
                                                        it,
                                                        userInfo.firstName + " " + userInfo.lastName,
                                                        true
                                                    )
                                                )
                                            }
                                        },
                                        {
                                           Log.e("Error loading ava", it.stackTraceToString(), it)
                                        }
                                    )
                                )
                            }
                        }
                    }
                },
                {
                    Log.e("Error loading blacklist", it.stackTraceToString(), it)
                }
            ))




        return fragmentView
    }

    override fun onDestroyView() {
        compositeDisposable.dispose()
        super.onDestroyView()
    }

}