package ru.hse.objectsmeterapp.service;

import com.sun.jna.ptr.IntByReference;
import ru.hse.objectsmeterapp.library.VisaLibrary;

public class MicranConnectService {

    public void connect() {
        IntByReference defaultRM = new IntByReference();
        int status = VisaLibrary.INSTANCE.viOpenDefaultRM(defaultRM);
        if (status != 0) {
            System.out.println("Ошибка viOpenDefaultRM: " + status);
            return;
        }

        String visaAddress = "TCPIP::localhost::8888::SOCKET::VNA";
        IntByReference instrument = new IntByReference();

        status = VisaLibrary.INSTANCE.viOpen(defaultRM.getValue(), visaAddress, 0, 20000, instrument);
        if (status != 0) {
            System.out.println("Ошибка viOpen: " + status);
            return;
        }

        String command = "*IDN?\n";
        byte[] writeBuffer = command.getBytes();
        IntByReference writeCount = new IntByReference();

        status = VisaLibrary.INSTANCE.viWrite(instrument.getValue(), writeBuffer, writeBuffer.length, writeCount);
        if (status != 0) {
            System.out.println("Ошибка viWrite: " + status);
            return;
        }

        byte[] readBuffer = new byte[1024];
        IntByReference readCount = new IntByReference();

        status = VisaLibrary.INSTANCE.viRead(instrument.getValue(), readBuffer, readBuffer.length, readCount);
        if (status != 0) {
            System.out.println("Ошибка viRead: " + status);
            return;
        }

        String response = new String(readBuffer, 0, readCount.getValue());
        System.out.println("Ответ от прибора: " + response);
    }
}
