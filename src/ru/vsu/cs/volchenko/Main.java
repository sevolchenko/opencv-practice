package ru.vsu.cs.volchenko;

import org.opencv.core.Core;
import ru.vsu.cs.volchenko.frame_main.FrameMain;

public class Main {

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) {

        new FrameMain().setVisible(true);

    }
}
