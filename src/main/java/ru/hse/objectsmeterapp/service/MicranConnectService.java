package ru.hse.objectsmeterapp.service;

import com.sun.jna.ptr.IntByReference;
import lombok.Getter;
import ru.hse.objectsmeterapp.exception.BusinessException;
import ru.hse.objectsmeterapp.library.VisaLibrary;
import ru.hse.objectsmeterapp.utils.NumbersUtils;

import java.nio.charset.StandardCharsets;

public class MicranConnectService {

    private static final String DEFAULT_ID = "---";

    @Getter
    private boolean connected = false;

    @Getter
    private String lastId = DEFAULT_ID;

    private int defaultRM;
    private int instrument;

    public void connect() {
        IntByReference defaultRMRef = new IntByReference();
        int status = VisaLibrary.INSTANCE.viOpenDefaultRM(defaultRMRef);
        if (status != 0) {
            connected = false;
        }

        this.defaultRM = defaultRMRef.getValue();
        IntByReference instrumentRef = new IntByReference();

        status = VisaLibrary.INSTANCE.viOpen(this.defaultRM, VisaLibrary.ADDRESS, 0, 20000, instrumentRef);
        if (status != 0) {
            VisaLibrary.INSTANCE.viClose(this.defaultRM);
            connected = false;
        }

        instrument = instrumentRef.getValue();
        VisaLibrary.INSTANCE.viSetAttribute(instrument, VisaLibrary.VI_ATTR_TMO_VALUE, 20000);

        visaWrite("FORM:DATA ASC\n");
        visaWrite("INIT:CONT OFF\n");
        visaWrite("*IDN?\n");

        connected = true;
        lastId = visaRead().trim();
    }

    public void createTraces() {
        try {
            visaWrite("CALCulate:PARameter:DELete:ALL\n");
            Thread.sleep(1000);

            visaWrite("CALCulate:PARameter:DEFine \"Trc1\",S11\n");
            Thread.sleep(1000);

            visaWrite("CALCulate:PARameter:DEFine \"Trc2\",S21\n");
            Thread.sleep(1000);

            visaWrite("CALCulate:PARameter:DEFine \"Trc3\",S12\n");
            Thread.sleep(1000);

            visaWrite("CALCulate:PARameter:DEFine \"Trc4\",S22\n");
            Thread.sleep(1000);
        } catch (Exception e) {
            connected = false;
            lastId = DEFAULT_ID;
            throw new BusinessException("Ошибка при создании трасс");
        }
    }

    public void makeInitialSettings(String startFrequency, String stopFrequency, String points, String frequencyAbbreviation) {
        Double startFrequencyHz = NumbersUtils.frequencyToHz(Double.parseDouble(startFrequency), frequencyAbbreviation);
        Double stopFrequencyHz = NumbersUtils.frequencyToHz(Double.parseDouble(stopFrequency), frequencyAbbreviation);
        try {
            visaWrite("SENSe:FREQuency:STARt " + startFrequencyHz + "\n");
            visaWrite("SENSe:FREQuency:STOP " + stopFrequencyHz + "\n");
            visaWrite("SENSe:SWEep:POINts " + points + "\n");
        } catch (Exception e) {
            connected = false;
            lastId = DEFAULT_ID;
            throw new BusinessException("Ошибка при начальных настройках");
        }
    }

    public String fetchMeasurementData(String traceName) {
        if (!connected) {
            return "";
        }

        try {
            visaWrite("CALC:PAR:SEL \"" + traceName + "\"\n");
            visaWrite("INIT:IMM\n");
            visaWrite("*OPC?\n");

            String ack = visaRead().trim();
            if (!ack.equals("+1")) {
                return "";
            }

            visaWrite("CALC:DATA? SDATA\n");

            return visaRead(new byte[64 * 1024]).trim();
        } catch (Exception e) {
            connected = false;
            lastId = DEFAULT_ID;
            throw new BusinessException("Ошибка при получении измерений, трасса: " + traceName);
        }
    }

    public void disconnect() {
        if (connected) {
            VisaLibrary.INSTANCE.viClose(instrument);
            VisaLibrary.INSTANCE.viClose(defaultRM);
            connected = false;
            lastId = DEFAULT_ID;
        }
    }

    private void visaWrite(String command) {
        byte[] buffer = command.getBytes(StandardCharsets.UTF_8);
        IntByReference writeCount = new IntByReference();
        int status = VisaLibrary.INSTANCE.viWrite(instrument, buffer, buffer.length, writeCount);

        if (status != 0) {
            connected = false;
            lastId = DEFAULT_ID;
            throw new BusinessException("Ошибка при отправке запроса к прибору, status: " + status);
        }
    }

    private String visaRead() {
        return visaRead(new byte[8192]);
    }

    private String visaRead(byte[] buffer) {
        IntByReference readCount = new IntByReference();
        int status = VisaLibrary.INSTANCE.viRead(instrument, buffer, buffer.length, readCount);

        if (status != 0 && status != 0x3FFF0005) {
            connected = false;
            lastId = DEFAULT_ID;
            throw new BusinessException("Ошибка при получении ответа от прибора, status: \" + status");
        }

        return new String(buffer, 0, readCount.getValue(), StandardCharsets.UTF_8);
    }
}
