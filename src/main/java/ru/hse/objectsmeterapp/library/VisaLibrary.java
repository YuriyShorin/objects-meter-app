package ru.hse.objectsmeterapp.library;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

public interface VisaLibrary extends Library {

    VisaLibrary INSTANCE = Native.load("MiVISA64", VisaLibrary.class);

    String ADDRESS = "TCPIP::localhost::8888::SOCKET::VNA";

    long VI_ATTR_TMO_VALUE = 0x3FFF001A;

    int viOpenDefaultRM(IntByReference vi);

    int viOpen(int sesn, String address, int mode, int timeout, IntByReference vi);

    int viWrite(int vi, byte[] buf, int count, IntByReference retCount);

    int viRead(int vi, byte[] buf, int count, IntByReference retCount);

    void viClose(int vi);

    int viSetAttribute(int vi, long attrName, int attrValue);
}
