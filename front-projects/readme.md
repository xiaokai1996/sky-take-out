# about this project
this is the front compiled project for sky-take-out

# how to run this project
put all the sky folder into nginx html folder, the hardest thing is to config the nginx
here is a tutorial about how to install nginx on m1 macbook https://blog.csdn.net/qq_23858785/article/details/123132765

# install nginx on m1 macbook

## download binary installers and dependent libraries
It needs to download 3 things and compile them all
```shell
1. nginx http://nginx.org/en/download.html
2. prce lib https://sourceforge.net/projects/pcre/files/
3. openssl https://www.openssl.org/source/
```
## compile and isntall nginx
```shell
cd nginx-1.18.0/
sudo ./configure --with-pcre=../pcre-8.45/ 
sudo make
sudo make install
# add to home path
vim ~/.zshrc
export PATH="/usr/local/nginx/sbin:$PATH"
source ~/.zshrc
# run nginx
cd /usr/local/nginx/sbin
sudo ./nginx
```

## Nginx config
```shell
cd /usr/local/nginx/conf
sudo vim nginx.conf
```
copy the sky folder to html folder, and set the root value as that path using vim
then reload the config via `sudo nginx reload`



## Nginx usage
use `sudo nginx` to start a nginx server
use `sudo nginx -s stop` to stop
use `sudo nginx -s reload` to load the configuration updates!
```shell
Options:
  -?,-h         : this help
  -v            : show version and exit
  -V            : show version and configure options then exit
  -t            : test configuration and exit
  -T            : test configuration, dump it and exit
  -q            : suppress non-error messages during configuration testing
  -s signal     : send signal to a master process: stop, quit, reopen, reload
  -p prefix     : set prefix path (default: /usr/local/nginx/)
  -e filename   : set error log file (default: logs/error.log)
  -c filename   : set configuration file (default: conf/nginx.conf)
  -g directives : set global directives out of configuration file
```

