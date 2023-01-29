package com.driver;

import java.util.*;

public class WhatsappService {
//    here key is mobileNumber and value is User OBJ
    Map<String,User> userHashMap = new HashMap<>();
//    here key is adminName and value is List of User in that group
    Map<String,List<User>> groupAndPersonalChatHashMap = new HashMap<>();
    Map<Group,List<Message>> groupMessageHashMap = new HashMap<>();
//    key will be group Name and value will be admin Name
    Map<String,String> groupNameAndAdminHashMap = new HashMap<>();
    int groupCount=0;
    int messageId=0;
    public String createUser(String name, String mobile) throws Exception {
       if(userHashMap.containsKey(mobile)){
           throw new Exception("User already exists");
       }
       User newUser = new User(name,mobile);
       userHashMap.put(mobile,newUser);
       return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
//        If List has only two user then its a personal chat
        if(users.size()==2){
            User admin = users.get(1);
            groupAndPersonalChatHashMap.put(admin.getName(),users);
            Group group = new  Group(admin.getName(), 2);
            groupNameAndAdminHashMap.put(group.getName(),admin.getName());
            return group;
        }
//        for group
        User admin = users.get(0);
        groupAndPersonalChatHashMap.put(admin.getName(),users);
        String groupName = "Group "+groupCount++;
        Group group = new Group(groupName,users.size());
        groupNameAndAdminHashMap.put(group.getName(),admin.getName());
        return group;
    }

    public int createMessage(String content) {
        Message message =new Message(messageId++,content,new Date());
        return message.getId();
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.
        if(!groupNameAndAdminHashMap.containsKey(group.getName())){
            throw new Exception("Group does not exist");
        }
        else if(!isSenderExistInGroup(group.getName(),sender)){
            throw new Exception("You are not allowed to send message");
        }
        List<Message> messageList = groupMessageHashMap.get(group);
        messageList.add(message);
        groupMessageHashMap.put(group,messageList);
        return messageList.size();
    }

    public boolean isSenderExistInGroup(String groupName,User sender){
        String adminName = groupNameAndAdminHashMap.get(groupName);
        List<User> groupUserList = groupAndPersonalChatHashMap.get(adminName);
        for(User user : groupUserList){
            if(user.equals(sender)){
                return true;
            }
        }
        return false;
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.
        if(!groupNameAndAdminHashMap.containsKey(group.getName())){
            throw new Exception("Group does not exist");
        }
//        groupNameAndAdminHashMap.put(admin.getName(),group.getName());
        else if(!Objects.equals(groupNameAndAdminHashMap.get(group.getName()), approver.getName())){
            throw new Exception("Approver does not have rights");
        } else if (!isSenderExistInGroup(group.getName(),user)) {
            throw new Exception("User is not a participant");
        }
        Group prevGroup = group;
//        i have to change in following hashMap groupAndPersonalChatHashMap,groupNameAndAdminHashMap

        //    here key is adminName and value is List of User in that group
        List<User> userList = groupAndPersonalChatHashMap.get(approver.getName());
        groupAndPersonalChatHashMap.remove(approver.getName());
        groupAndPersonalChatHashMap.put(user.getName(),userList);

//        Map<String,String> groupNameAndAdminHashMap = new HashMap<>();
        groupNameAndAdminHashMap.remove(prevGroup.getName());
        groupNameAndAdminHashMap.put(prevGroup.getName(),user.getName());

        return "SUCCESS";

    }

    public String findMessage(Date start, Date end, int k) {
        return null;
    }

    public int removeUser(User user) throws Exception {
        //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)

        if(groupNameAndAdminHashMap.containsValue(user.getName())){
           throw new Exception("Cannot remove admin");
        }

    }
}
