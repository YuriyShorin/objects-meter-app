package ru.hse.objectsmeterapp.service;

import com.sun.jna.ptr.IntByReference;
import ru.hse.objectsmeterapp.library.VisaLibrary;

import java.nio.charset.StandardCharsets;

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
            VisaLibrary.INSTANCE.viClose(defaultRM.getValue());
            return;
        }

        String command = "*IDN?\n";
        byte[] writeBuffer = command.getBytes(StandardCharsets.UTF_8);
        IntByReference writeCount = new IntByReference();

        status = VisaLibrary.INSTANCE.viWrite(instrument.getValue(), writeBuffer, writeBuffer.length, writeCount);
        if (status != 0) {
            System.out.println("Ошибка viWrite: " + status);
            cleanup(defaultRM.getValue(), instrument.getValue());
            return;
        }

        byte[] readBuffer = new byte[4096];
        IntByReference readCount = new IntByReference();

        status = VisaLibrary.INSTANCE.viRead(instrument.getValue(), readBuffer, readBuffer.length, readCount);
        if (status != 0 && status != 0x3FFF0005) {
            System.out.println("Ошибка viRead: " + status);
            cleanup(defaultRM.getValue(), instrument.getValue());
            return;
        }

        String response = new String(readBuffer, 0, readCount.getValue(), StandardCharsets.UTF_8);
        System.out.println("Ответ от прибора: " + response);

        //cleanup(defaultRM.getValue(), instrument.getValue());
    }

    private void cleanup(int defaultRM, int instrument) {
        VisaLibrary.INSTANCE.viClose(instrument);
        VisaLibrary.INSTANCE.viClose(defaultRM);
    }
}
