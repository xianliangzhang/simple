#!/bin/bash

######################################################################
#    本程序用于周期性生成指定目录中的文件列表，并将其拷贝到指定目录
######################################################################

IMAGE_DIR=/opt/spider/images/
IMAGE_INFO_FILE='image-list.html'
TOMCAT_HOME='/opt/tomcat/webapps/ROOT/imgs-info.html'
INTERVAL=10m

function generateHtml() {
  for line in $(find images -type f -size +100k); do
    echo "<a href=http://kome.sexy/spider/images/${line:7}>${line:7}</a>" >> $IMAGE_INFO_FILE 
  done 
}

while (true); do 
  echo  "Start - $(date +'%Y-%m-%d %H:%M:%S')"
  
  # 清理文件内容
  > $IMAGE_INFO_FILE
    
  # 开始写内容
  generateHtml 
   
  # 拷贝文件到指定目录
  cp $IMAGE_INFO_FILE $TOMCAT_HOME

  echo  "Finish - $(date +'%Y-%m-%d %H:%M:%S')"
  
  # 工作完成，小歇一会儿再做一次
  sleep $INTERVAL
done
