/**
 * Created by nimdanoob on 2016/9/30.
 */

var express = require('express')
var superagent = require('superagent')
var cheerio = require('cheerio')

var app = express()

app.get('/', function (req, res, next) {
    superagent.get('https://cnodejs.org')
        .end(function (err, sres) {
            //常规的错误处理
            if (err) {
                return next(err)
            }

            var $ = cheerio.load(sres.text)
            var items = [];
            $('#topic_list .topic_title').each(function (idx, element) {
                var $element = $(element)
                items.push({
                    title: $element.attr('title'),
                    href: $element.attr('href')
                });
            });

            res.send(items)

        })

})

app.listen(3000,function (req, res) {
    console.log('start success!')
})