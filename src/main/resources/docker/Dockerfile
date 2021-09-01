FROM openjdk:8-jre-alpine
#FROM pig4cloud/java:8-jre

MAINTAINER mengdexuan

ENV TZ=Asia/Shanghai

VOLUME /tmp

#安装 ffmpeg 音频处理软件
RUN echo "http://mirrors.aliyun.com/alpine/v3.6/main" > /etc/apk/repositories \
    && echo "http://mirrors.aliyun.com/alpine/v3.6/community" >> /etc/apk/repositories \
    && apk update upgrade \
    && apk add --no-cache procps unzip curl bash tzdata \
	&& apk add yasm && apk add ffmpeg \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir -p /app
RUN mkdir -p /app/data
RUN mkdir -p /app/photo

WORKDIR /app

EXPOSE 2101

ADD ./audio.jar /app/app.jar
ADD ./ffprobe.sh /app/ffprobe.sh
ADD ./ffmpeg.sh /app/ffmpeg.sh
ADD ./config /app/config

ENTRYPOINT  ["java","-Xms256m","-Xmx256m","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar","--spring.config.location=/app/config/application.yml","--logging.config=/app/config/logback-spring.xml"]

