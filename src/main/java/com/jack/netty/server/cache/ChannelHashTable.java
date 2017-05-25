package com.jack.netty.server.cache;

import io.netty.channel.Channel;

import java.util.Hashtable;


public class ChannelHashTable {

    private static Hashtable<String, Channel> busiTable = new Hashtable<String, Channel>();

    private static Hashtable<Integer, String> channelTable = new Hashtable<Integer, String>();


    public static Channel getCtx(String busiId) {
        return busiTable.get(busiId);
    }

    public static void put(String busiId, Channel channel) {
        busiTable.put(busiId, channel);
        channelTable.put(channel.hashCode(), busiId);
    }

    public static boolean exists(String busiId) {
        if (busiTable.contains(busiId)) {
            return true;
        } else {
            return false;
        }
    }

    public static void remove(Integer channelId) {
        if (channelTable.contains(channelId)) {
            String busiId = channelTable.get(channelId);
            busiTable.remove(busiId);
            channelTable.remove(channelId);
        }
    }

    public static boolean existsWritableChannel(String busiId) {
        if (busiTable.contains(busiId) && busiTable.get(busiId).isWritable()) {
            return true;
        } else {
            return false;
        }
    }

}
