#!/bin/sh
# usage:
#        ./profile.sh appfarm.vap.me
curl -X POST -d @100.001.xml $1/1000/0001 -H "Content-Type:text/xml" -v
curl -X POST -d @100.002.xml $1/1000/0002 -H "Content-Type:text/xml" -v
curl -X POST -d @100.003.xml $1/1000/0003 -H "Content-Type:text/xml" -v
curl -X POST -d @100.004.xml $1/1000/0004 -H "Content-Type:text/xml" -v
curl -X POST -d @100.005.xml $1/1000/0005 -H "Content-Type:text/xml" -v
curl -X POST -d @100.006.xml $1/1000/0006 -H "Content-Type:text/xml" -v
curl -X POST -d @100.007.xml $1/1000/0007 -H "Content-Type:text/xml" -v
curl -X POST -d @100.008.xml $1/1000/0008 -H "Content-Type:text/xml" -v
curl -X POST -d @100.009.xml $1/1000/0009 -H "Content-Type:text/xml" -v
curl -X POST -d @100.010.xml $1/1000/0010 -H "Content-Type:text/xml" -v
curl -X POST -d @100.101.xml $1/1000/0101 -H "Content-Type:text/xml" -v
curl -X POST -d @100.102.xml $1/1000/0102 -H "Content-Type:text/xml" -v
curl -X POST -d @100.103.xml $1/1000/0103 -H "Content-Type:text/xml" -v
curl -X POST -d @100.104.xml $1/1000/0104 -H "Content-Type:text/xml" -v
curl -X POST -d @100.105.xml $1/1000/0105 -H "Content-Type:text/xml" -v
curl -X POST -d @100.106.xml $1/1000/0106 -H "Content-Type:text/xml" -v
curl -X POST -d @100.107.xml $1/1000/0107 -H "Content-Type:text/xml" -v
curl -X POST -d @100.108.xml $1/1000/0108 -H "Content-Type:text/xml" -v
curl -X POST -d @100.109.xml $1/1000/0109 -H "Content-Type:text/xml" -v
curl -X POST -d @100.110.xml $1/1000/0110 -H "Content-Type:text/xml" -v

