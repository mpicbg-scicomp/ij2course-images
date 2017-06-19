package de.mpicbg.scf.rhaase.fiji.ij2course.images;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.NewImage;
import net.imagej.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 *
 *
 * Author: Robert Haase, Scientific Computing Facility, MPI-CBG Dresden, 
 *         rhaase@mpi-cbg.de
 * Date: May 2017
 * 
 * Copyright 2017 Max Planck Institute of Molecular Cell Biology and Genetics, 
 *                Dresden, Germany
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright notice, 
 *      this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright 
 *      notice, this list of conditions and the following disclaimer in the 
 *      documentation and/or other materials provided with the distribution.
 *   3. Neither the name of the copyright holder nor the names of its 
 *      contributors may be used to endorse or promote products derived from 
 *      this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */ 
public class ImagesMain {

    public static <T extends RealType<T>> void main(String... args) {

        // now we run ImageJ2 instead of IJ1
        ImageJ ij = new net.imagej.ImageJ();
        ij.ui().showUI();


        ImagePlus testImage = IJ.openImage("src/main/resources/mitosis.tif");
        testImage.show();

        //// inside ImageJ we could run
        // IJ.run(testImage, "Normalisation", "");
        // ImagePlus resultImage = IJ.getImage();
        //// but from within the IDE we need to do that:
        ImagePlus resultImage = ImageNormalizerPlugin.normalize(testImage);
        resultImage.setTitle("Result using IJ1");
        resultImage.show();

        // run the new plugin in the old fashioned way
        IJ.run(testImage, "Normalisation (IJ2)", "");
        ImagePlus resultImageIJ2 = IJ.getImage();
        resultImageIJ2.setTitle("Result using IJ.run and IJ2 plugin");

        // this is a bridge to imagej2
        Img<T> testImg = ImageJFunctions.wrapReal(testImage);

        // this is the cool new imagej2
        ij.ui().show(testImg);
        ij.command().run(ImageNormalizerIJ2Plugin.class, false, new Object[]{"input", testImg, "ij", ij});

        ImagePlus resultImagePureIJ2 = IJ.getImage();
        resultImagePureIJ2.setTitle("Result using pure IJ2");


    }
}
