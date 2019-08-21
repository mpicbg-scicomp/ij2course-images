package de.mpicbg.scf.rhaase.fiji.ij2course.images;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Random;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;

import org.junit.Test;

/**
 * Author: Robert Haase, Scientific Computing Facility, MPI-CBG Dresden,
 * rhaase@mpi-cbg.de
 * Date: June 2017
 * <p>
 * Copyright 2017 Max Planck Institute of Molecular Cell Biology and Genetics,
 * Dresden, Germany
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
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
 */
public class ImageNormalizerIJ2PluginTest {

    private double tolerance = 0.0001;

    @Test
    public <T extends RealType<T>> void testIJ2Normalisation() {
        ImageJ ij = new ImageJ();

        // create random image
        final Random r = new Random();
        Img<UnsignedByteType> testImage = ArrayImgs.unsignedBytes(1000, 1000);
        for (UnsignedByteType sample : testImage) {
            sample.setInteger(r.nextInt(256));
        }

        // normalize it
        long timeStamp = System.currentTimeMillis();
        Img<FloatType> resultImage = ImageNormalizerIJ2Plugin.normalize(testImage);
        System.out.println("Normalisation took " + (System.currentTimeMillis() - timeStamp) + " msec");

        // check if normalisation has reasonable result statistics
        Pair<UnsignedByteType, UnsignedByteType> minMaxBefore = //
            ij.op().stats().minMax(testImage);
        Pair<FloatType, FloatType> minMaxAfter = //
            ij.op().stats().minMax(resultImage);
        double minBefore = minMaxBefore.getA().getRealDouble();
        double maxBefore = minMaxBefore.getB().getRealDouble();
        double minAfter = minMaxAfter.getA().getRealDouble();
        double maxAfter = minMaxAfter.getB().getRealDouble();
        double meanBefore = ij.op().stats().mean(testImage).getRealDouble();
        double meanAfter = ij.op().stats().mean(resultImage).getRealDouble();

        assertEquals(0.0, minAfter, tolerance);
        assertEquals(1.0, maxAfter, tolerance);
        assertEquals((meanBefore - minBefore) / (maxBefore - minBefore),
                meanAfter, tolerance);

        ij.context().dispose();
    }

    @Test
    public void testIfDimensionsMatch() throws IOException {
        ImageJ ij = new ImageJ();
        Dataset testImage = ij.scifio().datasetIO().open("src/main/resources/mitosis.tif");
        // normalize it
        long timeStamp = System.currentTimeMillis();
        Img<FloatType> resultImage = ImageNormalizerIJ2Plugin.normalize((Img) testImage);
        System.out.println("Normalisation took " + (System.currentTimeMillis() - timeStamp) + " msec");

        // check result image dimensions
        for (int d = 0; d < testImage.numDimensions(); d++) {
            assertEquals(testImage.dimension(d), resultImage.dimension(d));
        }
        ij.context().dispose();
    }
}