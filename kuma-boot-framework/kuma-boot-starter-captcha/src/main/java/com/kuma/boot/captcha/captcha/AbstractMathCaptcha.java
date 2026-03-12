//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.captcha.captcha;

import java.util.Random;

public abstract class AbstractMathCaptcha extends BaseCaptcha {
    protected char[] alphas() {
        Random random = new Random();
        char[] cs = new char[4];
        int rand0 = random.nextInt(10);
        if (rand0 == 0) {
            rand0 = 1;
        }

        int rand1 = random.nextInt(10);
        boolean rand2 = random.nextBoolean();
        int rand3 = random.nextInt(10);
        cs[0] = (char)(48 + rand0);
        cs[1] = (char)(48 + rand1);
        cs[2] = (char)(rand2 ? 43 : 45);
        cs[3] = (char)(48 + rand3);
        int num1 = rand0 * 10 + rand1;
        int result = rand2 ? num1 + rand3 : num1 - rand3;
        this.chars = String.valueOf(result);
        return cs;
    }
}
