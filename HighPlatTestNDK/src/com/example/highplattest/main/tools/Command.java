package com.example.highplattest.main.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Command {
    /**
     * 命令头（1字节） + 命令事情（1字节） + 命令组（1字节） + 数据长度（2字节）+ 数据
     *
     */

    private static final byte PACKET_TYPE = 0x19;

    private byte command;

    private byte group;

//    private int len;

    private byte[] data;

    public byte getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = (byte)(command&0xFF);
    }

    public byte getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = (byte)(group&0xFF);
    }

    public int getLen() {
        int len = 0;
        if(data != null){
            len = data.length;
        }
        return len;
    }

//    public void setLen(int len) {
//        this.len = len;
//    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] pack()
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int len = getLen();
        bout.write(PACKET_TYPE);
        bout.write(command);
        bout.write(group);
        bout.write(len&0xFF);
        bout.write((len>>8)&0xFF);

        if(len > 0){
            try {
                bout.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] result = bout.toByteArray();
        try {
            bout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Command decode(byte[] data){
        if(data.length < 5){
            return null;
        }

        if(data[0] != PACKET_TYPE){
            return null;
        }

        byte command = data[1];
        byte group = data[2];

        int len = (data[3]&0xFF)|((data[4]&0xFF)<<8);

        if(data.length < 5+len){
            return null;
        }

        byte[] cdata = new byte[len];

        if(len > 0){
            System.arraycopy(data, 5, cdata, 0, len);
        }

        Command c = new Command();
        c.command = command;
        c.group = group;
        c.data = cdata;

        return c;
    }

    public static Command generateCommand(int command, int group, byte[] data){
        Command genCommand = new Command();
        genCommand.setCommand(command);
        genCommand.setGroup(group);
        genCommand.setData(data);
        return genCommand;
    }

}

