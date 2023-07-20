package com.leviathan.messenger_i_tochka.utils;

import java.util.List;

public class SystemMessages {
     private static final String USER_JOIN_CHANNEL_TEMPLATE = "%s has joined the channel";
     private static final String USER_LEFT_CHANNEL_TEMPLATE = "%s has left the channel";
     private static final String CHANNEL_CREATED_TEMPLATE = "Channel '%s' was created";
     private static final String CHAT_CREATED_TEMPLATE = "Chat with users: %s - was created";
     private static final String USER_JOIN_CHAT_TEMPLATE = "%s has joined the chat";
     private static final String USER_LEFT_CHAT_TEMPLATE = "%s has left the chat";

     public static String getChannelCreatedMessage(String channelName) {
         return String.format(CHANNEL_CREATED_TEMPLATE, channelName);
     }

     public static String getUserJoinChannelMessage(String username) {
         return String.format(USER_JOIN_CHANNEL_TEMPLATE, username);
     }

     public static String getChatCreatedMessage(List<String> usernames) {
         return String.format(CHAT_CREATED_TEMPLATE, usernames.toString().replace("[", "").replace("]", ""));
     }

     public static String getUserLeftChannelMessage(String username) {
         return String.format(USER_LEFT_CHANNEL_TEMPLATE, username);
     }
}
