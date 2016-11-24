package com.willer.rmi;

import com.willer.rmi.inter.transfer.ITransferService;
import com.willer.rmi.skeleton.ClientRepertory;

import java.io.FileInputStream;

/**
 * Created by Hack on 2016/11/23.
 */
public class Client {

    public static void main(String[] args) throws Exception {
        String file = args.length == 0 ? "D:\\WeChat1.5.exe" : args[0];
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bufferSize = -1;
        while ((bufferSize = inputStream.read(buffer)) != -1) {
            ((ITransferService) ClientRepertory.lookup("TransferService")).transfer(buffer, bufferSize);
        }

        System.out.println("UPLOADED...");
    }
}
