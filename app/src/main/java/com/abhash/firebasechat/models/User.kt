package com.abhash.firebasechat.models
class User(val uid:String, val username:String, val profileImageUrl:String){
    constructor():this("","","")
}