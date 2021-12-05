package com.hiklife.kvm;

import com.hiklife.kvm.net.TCPServer;

public class TcpServerMain {
    public static void main(String[] args){
        TCPServer server = new TCPServer(5900);
        server.start();
    }
}
