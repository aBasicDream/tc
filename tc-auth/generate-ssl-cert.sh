#!/bin/bash

# SSL证书生成脚本
# 用于生成HTTPS服务所需的SSL证书

echo "正在生成SSL证书..."

# 创建证书目录
mkdir -p ssl

# 生成私钥
openssl genrsa -out ssl/tc-auth.key 2048

# 生成证书签名请求
openssl req -new -key ssl/tc-auth.key -out ssl/tc-auth.csr -subj "/C=CN/ST=Beijing/L=Beijing/O=TC System/OU=IT Department/CN=tc-auth.local"

# 生成自签名证书
openssl x509 -req -days 365 -in ssl/tc-auth.csr -signkey ssl/tc-auth.key -out ssl/tc-auth.crt

# 生成PKCS12格式证书
openssl pkcs12 -export -in ssl/tc-auth.crt -inkey ssl/tc-auth.key -out ssl/keystore.p12 -name tc-auth -password pass:tc123456

# 复制证书到resources目录
cp ssl/keystore.p12 tc-auth/tc-auth-service/src/main/resources/

echo "SSL证书生成完成！"
echo "证书文件位置: tc-auth/tc-auth-service/src/main/resources/keystore.p12"
echo "证书密码: tc123456"
echo "证书别名: tc-auth"

# 显示证书信息
echo ""
echo "证书信息:"
openssl x509 -in ssl/tc-auth.crt -text -noout | grep -E "(Subject:|Not Before:|Not After:)"

echo ""
echo "请将以下内容添加到hosts文件中:"
echo "127.0.0.1 tc-auth.local"
