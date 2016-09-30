/**
 * Created by nimdanoob on 2016/9/30.
 */

var express = require('express')
var utility = require('utility')

var app = express()
app.get('/', function (req, res) {
    var q= req.query.q;
    var md5Value = utility.md5(q);
    res.send(md5Value)
})

app.listen(3000,function (req,res) {
    console.log('app is runging at port 300')

})