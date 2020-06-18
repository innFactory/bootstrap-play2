  
#!/bin/bash

echo "Downloading AWS IAM Authenticator"
curl -s -S -L -o aws-iam-authenticator https://amazon-eks.s3.us-west-2.amazonaws.com/1.16.8/2020-04-16/bin/linux/amd64/aws-iam-authenticator
echo "Successfully downloaded AWS IAM Authenticator"
chmod +x ./aws-iam-authenticator
mkdir -p $HOME/bin && cp ./aws-iam-authenticator $HOME/bin/aws-iam-authenticator && export PATH=$PATH:$HOME/bin
echo "::add-path::$HOME/bin"