/**
 * Created by nimdanoob on 2016/9/30.
 */

var eventprox = require('eventproxy')
var superagent = require('superagent')
var cheerio = require('cheerio')
var url = require('url')

var cnodeUrl = 'https://cnodejs.org';

superagent.get(cnodeUrl)
    .end(function (err, res) {
        if (err){
            return console.log(err)
        }
        var topicUrls = [];
        console.log('得到文本' + res.text);
        var $ = cheerio.load(res.text);
        $('#topic_list .topic_title').each(function (idx, element) {
            var $element = $(element);
            var href = url.resolve(cnodeUrl,$element.attr('href'));
            topicUrls.push(href)
        });

        var ep = new eventprox();

        ep.after('topic_html',topicUrls.length,function (topicks) {
            topicks = topicks.map(function (topicPair) {
                var topicUrl = topicPair[0];
                var topicHtml = topicPair[1];
                var $ = cheerio.load(topicHtml)
                return ({
                    title: $('.topic_full_title').text().trim(),
                    href: topicUrl,
                    comment: $('.reply_content').eq(0).text().trim(),
                });
            });
            console.log('fianl');
            console.log(topicks);
        });

        topicUrls.forEach(function (topicUrl) {
            console.log('http  请求 获取' + topicUrl + '的内容\n');
            superagent.get(topicUrl)
                .end(function (err, res) {
                    console.log('http  请求 获取' + topicUrl + '的内容 >>>>>>成功\n');
                    console.log('fetch' + topicUrl + 'successful');
                    ep.emit('topic_html',[topicUrl,res.text])
                })
        })
    })

