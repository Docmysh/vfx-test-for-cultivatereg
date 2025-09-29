package Vfx.vfx_test_for_cultivatereg.util;

import net.minecraft.world.phys.Vec3;

public final class TornadoShape {
    private TornadoShape() {
    }

    public static Vec3 toWorld(TornadoPoint point, double centerX, double centerY, double centerZ,
                               float radius, float height, double rotationRadians) {
        double scaledX = point.x() * radius;
        double scaledZ = point.z() * radius;
        double cos = Math.cos(rotationRadians);
        double sin = Math.sin(rotationRadians);
        double rotatedX = scaledX * cos - scaledZ * sin;
        double rotatedZ = scaledX * sin + scaledZ * cos;
        double scaledY = point.y() * height;
        return new Vec3(centerX + rotatedX, centerY + scaledY, centerZ + rotatedZ);
    }

    public static record TornadoPoint(float x, float y, float z) {
    }

    public static final TornadoPoint[] POINTS = new TornadoPoint[]{
            new TornadoPoint(0.000000F, 0.000000F, 0.000000F),
            new TornadoPoint(0.009293F, 0.010101F, 0.003939F),
            new TornadoPoint(0.014091F, 0.020202F, 0.014495F),
            new TornadoPoint(0.010960F, 0.030303F, 0.028232F),
            new TornadoPoint(-0.001162F, 0.040404F, 0.040404F),
            new TornadoPoint(-0.021010F, 0.050505F, 0.045909F),
            new TornadoPoint(-0.044697F, 0.060606F, 0.040960F),
            new TornadoPoint(-0.066616F, 0.070707F, 0.023687F),
            new TornadoPoint(-0.080656F, 0.080808F, -0.004697F),
            new TornadoPoint(-0.081515F, 0.090909F, -0.040252F),
            new TornadoPoint(-0.066010F, 0.101010F, -0.076465F),
            new TornadoPoint(-0.034141F, 0.111111F, -0.105757F),
            new TornadoPoint(0.010606F, 0.121212F, -0.120757F),
            new TornadoPoint(0.061515F, 0.131313F, -0.116010F),
            new TornadoPoint(0.109697F, 0.141414F, -0.089293F),
            new TornadoPoint(0.145505F, 0.151515F, -0.042323F),
            new TornadoPoint(0.160505F, 0.161616F, 0.018838F),
            new TornadoPoint(0.149293F, 0.171717F, 0.084848F),
            new TornadoPoint(0.110606F, 0.181818F, 0.144293F),
            new TornadoPoint(0.048232F, 0.191919F, 0.185757F),
            new TornadoPoint(-0.029394F, 0.202020F, 0.199848F),
            new TornadoPoint(-0.110151F, 0.212121F, 0.181262F),
            new TornadoPoint(-0.180252F, 0.222222F, 0.130000F),
            new TornadoPoint(-0.226464F, 0.232323F, 0.051768F),
            new TornadoPoint(-0.238737F, 0.242424F, -0.042273F),
            new TornadoPoint(-0.211868F, 0.252525F, -0.137374F),
            new TornadoPoint(-0.147323F, 0.262626F, -0.217424F),
            new TornadoPoint(-0.052980F, 0.272727F, -0.267525F),
            new TornadoPoint(0.057424F, 0.282828F, -0.276919F),
            new TornadoPoint(0.166464F, 0.292929F, -0.241010F),
            new TornadoPoint(0.255707F, 0.303030F, -0.162576F),
            new TornadoPoint(0.308787F, 0.313131F, -0.051869F),
            new TornadoPoint(0.314444F, 0.323232F, 0.074848F),
            new TornadoPoint(0.268636F, 0.333333F, 0.197373F),
            new TornadoPoint(0.175757F, 0.343434F, 0.295050F),
            new TornadoPoint(0.048333F, 0.353535F, 0.350202F),
            new TornadoPoint(-0.094495F, 0.363636F, 0.351161F),
            new TornadoPoint(-0.230000F, 0.373737F, 0.294596F),
            new TornadoPoint(-0.335353F, 0.383838F, 0.186717F),
            new TornadoPoint(-0.391666F, 0.393939F, 0.042424F),
            new TornadoPoint(-0.386919F, 0.404040F, -0.116313F),
            new TornadoPoint(-0.318888F, 0.414141F, -0.264242F),
            new TornadoPoint(-0.195454F, 0.424242F, -0.376565F),
            new TornadoPoint(-0.034192F, 0.434343F, -0.432979F),
            new TornadoPoint(0.140353F, 0.444444F, -0.421717F),
            new TornadoPoint(0.300151F, 0.454545F, -0.341363F),
            new TornadoPoint(0.418484F, 0.464646F, -0.201919F),
            new TornadoPoint(0.474141F, 0.474747F, -0.023535F),
            new TornadoPoint(0.455403F, 0.484848F, 0.166464F),
            new TornadoPoint(0.362020F, 0.494949F, 0.337525F),
            new TornadoPoint(0.206111F, 0.505051F, 0.461060F),
            new TornadoPoint(0.010505F, 0.515152F, 0.515050F),
            new TornadoPoint(-0.194646F, 0.525253F, 0.487828F),
            new TornadoPoint(-0.376363F, 0.535354F, 0.380707F),
            new TornadoPoint(-0.504242F, 0.545455F, 0.207980F),
            new TornadoPoint(-0.555555F, 0.555556F, -0.004899F),
            new TornadoPoint(-0.519040F, 0.565657F, -0.224899F),
            new TornadoPoint(-0.397474F, 0.575758F, -0.416565F),
            new TornadoPoint(-0.207424F, 0.585859F, -0.547878F),
            new TornadoPoint(0.022677F, 0.595960F, -0.595504F),
            new TornadoPoint(0.257070F, 0.606061F, -0.548838F),
            new TornadoPoint(0.458030F, 0.616162F, -0.412171F),
            new TornadoPoint(0.591918F, 0.626263F, -0.204545F),
            new TornadoPoint(0.634949F, 0.636364F, 0.042778F),
            new TornadoPoint(0.577171F, 0.646465F, 0.291212F),
            new TornadoPoint(0.424747F, 0.656566F, 0.500656F),
            new TornadoPoint(0.199242F, 0.666667F, 0.636211F),
            new TornadoPoint(-0.065202F, 0.676768F, 0.673635F),
            new TornadoPoint(-0.327171F, 0.686869F, 0.603939F),
            new TornadoPoint(-0.544393F, 0.696970F, 0.435151F),
            new TornadoPoint(-0.680605F, 0.707071F, 0.191565F),
            new TornadoPoint(-0.711514F, 0.717172F, -0.089899F),
            new TornadoPoint(-0.629090F, 0.727273F, -0.364949F),
            new TornadoPoint(-0.443383F, 0.737374F, -0.589191F),
            new TornadoPoint(-0.181414F, 0.747475F, -0.725151F),
            new TornadoPoint(0.116869F, 0.757576F, -0.748484F),
            new TornadoPoint(0.404444F, 0.767677F, -0.652524F),
            new TornadoPoint(0.634848F, 0.777778F, -0.449343F),
            new TornadoPoint(0.769595F, 0.787879F, -0.168788F),
            new TornadoPoint(0.784494F, 0.797980F, 0.146060F),
            new TornadoPoint(0.674140F, 0.808081F, 0.445605F),
            new TornadoPoint(0.452979F, 0.818182F, 0.681363F),
            new TornadoPoint(0.153737F, 0.828283F, 0.813888F),
            new TornadoPoint(-0.177475F, 0.838384F, 0.819393F),
            new TornadoPoint(-0.488333F, 0.848485F, 0.693837F),
            new TornadoPoint(-0.728585F, 0.858586F, 0.454242F),
            new TornadoPoint(-0.857928F, 0.868687F, 0.136262F),
            new TornadoPoint(-0.853080F, 0.878788F, -0.211010F),
            new TornadoPoint(-0.711666F, 0.888889F, -0.532626F),
            new TornadoPoint(-0.453181F, 0.898990F, -0.776413F),
            new TornadoPoint(-0.116313F, 0.909091F, -0.901615F),
            new TornadoPoint(0.246666F, 0.919192F, -0.885453F),
            new TornadoPoint(0.578282F, 0.929293F, -0.727423F),
            new TornadoPoint(0.824797F, 0.939394F, -0.449646F),
            new TornadoPoint(0.944847F, 0.949495F, -0.093939F),
            new TornadoPoint(0.916463F, 0.959596F, 0.284394F),
            new TornadoPoint(0.741110F, 0.969697F, 0.625353F),
            new TornadoPoint(0.443636F, 0.979798F, 0.873585F),
            new TornadoPoint(0.069141F, 0.989899F, 0.987473F),
            new TornadoPoint(-0.324141F, 1.000000F, 0.946009F)
    };
}