
package com.example.flickpicks.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.PartyGroup
import com.example.flickpicks.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMemberViewModel @Inject constructor(

) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    // Party group state
    private val _partyGroup = MutableStateFlow<PartyGroup?>(null)
    val partyGroup: StateFlow<PartyGroup?> = _partyGroup

    private val _memberNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val memberNames: StateFlow<Map<String, String>> = _memberNames.asStateFlow()

    // List of searched users
    private val _userList = MutableStateFlow<List<UserProfile>>(emptyList())
    val userList: StateFlow<List<UserProfile>> = _userList

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _userList.value = emptyList()
            return
        }

        firestore.collection("users")
            .orderBy("userName")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.mapNotNull {
                    it.toObject(UserProfile::class.java)
                }
                _userList.value = users
            }
            .addOnFailureListener {
                _userList.value = emptyList()
            }
    }

    fun fetchMemberNames(memberIds: List<String>) {
        viewModelScope.launch {
            val userMap = mutableMapOf<String, String>()
            firestore.collection("users").whereIn("id", memberIds).get()
                .addOnSuccessListener { documents ->
                    for (doc in documents) {
                        val user = doc.toObject(UserProfile::class.java)
                        userMap[user.id] = user.userName  // Store user name instead of ID
                    }
                    _memberNames.value = userMap
                }
        }
    }

    fun loadPartyGroup(groupId: Int) {
        viewModelScope.launch {
            firestore.collection("party_groups")
                .document(groupId.toString())
                .get()
                .addOnSuccessListener { document ->
                    val members = document.get("members") as? List<String> ?: emptyList()
                    _partyGroup.value = PartyGroup(id = groupId, members = members.toMutableList())
                    fetchMemberNames(members)
                }
                .addOnFailureListener {
                    _partyGroup.value = null
                }
        }
    }

    fun addMemberToGroup(groupId: Int, userId: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val groupRef = firestore.collection("party_groups").document(groupId.toString())
            val userRef = firestore.collection("users").document(userId)

            groupRef.get().addOnSuccessListener { document ->
                val partyGroup = document.toObject(PartyGroup::class.java)

                if (partyGroup == null) {
                    onComplete(false, "Failed to get group data")
                    return@addOnSuccessListener
                }

                if (partyGroup.members.contains(userId)) {
                    onComplete(false, "User is already a member!")
                    return@addOnSuccessListener
                }


                partyGroup.members.add(userId)


                groupRef.set(partyGroup)
                    .addOnSuccessListener {
                        userRef.get().addOnSuccessListener { userDoc ->
                            val userProfile = userDoc.toObject(UserProfile::class.java)

                            if (userProfile != null) {
                                val updatedGroups = userProfile.partyGroups.toMutableList()


                                if (!updatedGroups.any { it.id == partyGroup.id }) {
                                    updatedGroups.add(partyGroup)
                                }

                                userRef.update("partyGroups", updatedGroups)
                                    .addOnSuccessListener {
                                        _partyGroup.value = partyGroup.copy(members = partyGroup.members.toMutableList())
                                        val updatedMemberNames = _memberNames.value.toMutableMap()
                                        updatedMemberNames[userId] = userProfile.userName
                                        _memberNames.value = updatedMemberNames
                                        onComplete(true, "User added successfully!")
                                    }
                                    .addOnFailureListener {
                                        onComplete(false, "Failed to update user data")
                                    }
                            }
                        }
                    }
                    .addOnFailureListener {
                        onComplete(false, "Failed to add user to group")
                    }
            }.addOnFailureListener {
                onComplete(false, "Failed to retrieve group data")
            }
        }
    }
}






