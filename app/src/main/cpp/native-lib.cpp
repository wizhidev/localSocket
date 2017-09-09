#include <jni.h>
#include <string>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <android/log.h>

#define LOG    "nativeLib-jni" // 这个是自定义的LOG的标识
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG,__VA_ARGS__) // 定义LOGF类型

int send_remote_request(char *msg);

extern "C"
JNIEXPORT jstring

JNICALL
Java_etsoft_localsocket_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    send_remote_request("hello from C++");
    return env->NewStringUTF(hello.c_str());
}

int send_remote_request(char *msg) {
    LOGD("send_remote_request \n");
    int localsocket, len;
    struct sockaddr_un remote;

    if ((localsocket = socket(AF_UNIX, SOCK_STREAM, 0)) == -1) {
        LOGD("socket error \n");
        return -1;
    }

    char *name = "local.socket.address.listen.native.cmd";//与java上层相同哦

    remote.sun_path[0] = '\0'; /* abstract namespace */
    strcpy(remote.sun_path + 1, name);
    remote.sun_family = AF_UNIX;
    int nameLen = strlen(name);
    len = 1 + nameLen + offsetof(struct sockaddr_un, sun_path);

    if (connect(localsocket, (struct sockaddr *) &remote, len) == -1) {
        LOGD("connect error \n");
        return -1;
    }

    if (send(localsocket, msg, strlen(msg), 0) == -1) {
        LOGD("send error \n");
        return -1;
    }

    close(localsocket);
    LOGD("send_remote_request complete\n");
    return 0;
}