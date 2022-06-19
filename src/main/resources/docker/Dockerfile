FROM fiadliel/java8-jre

MAINTAINER mengdexuan

ENV TZ=Asia/Shanghai

VOLUME /tmp

RUN apk update upgrade \
    && apk add --no-cache procps unzip curl bash tzdata \
	&& apk add yasm && apk add ffmpeg \
    && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir -p /app

WORKDIR /app

EXPOSE 6004

#设置环境变量
ENV DB_PASSWORD 7b639421-bg2hyw2dfg-3e681b10-051f4
ENV accessKeyId LTAI5tKPxhXXWokRkeGkyKxf
ENV accessKeySecret SFduIX5oig4s5joNlHSkO3Hoak1mtm
ENV ossEndpoint oss-cn-hongkong.aliyuncs.com
ENV ossBucketName biyabizzan



ADD ./market.jar /app/app.jar
ADD ./config /app/config

ENTRYPOINT  ["java","-Xms256m","-Xmx256m","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar","--spring.config.location=/app/config/application.properties","--logging.config=/app/config/logback-spring.xml"]


