#include <stdlib.h>
#include <android/log.h>
#include "pinul.h"

extern calculate_pi(int digits);

JNIEXPORT jint JNICALL Java_ca_littlebox_misc_pinul_PiNul_pi(
    JNIEnv *env, jobject jobj, jint digits) {
    return (jint) calculate_pi((int)digits);
} 

// Le deréférencement d'un nul est nul.
JNIEXPORT void JNICALL Java_ca_littlebox_misc_pinul_PiNul_nul(
    JNIEnv *env, jobject jobj) {
    __android_log_write(ANDROID_LOG_DEBUG, "PiNul", "About to crash.");
    int *p = (int *) NULL;
    *p = 12345;
}

