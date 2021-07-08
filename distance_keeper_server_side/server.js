const express = require('express');
const bodyParser = require('body-parser');
const querystring = require('querystring');
const { resolve } = require('path');

let dbConn = null;
const { DBHandler } = require('./Handler/DBHandler')

const PORT = process.env.PORT || 8080;
//const PORT = 1234;

const app = express();
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

app.post('/api/menu', async function(req, res){
    var mac = req.body.beaconmac;

    const param = { "beaconmac": mac };
    let records = await dbConn.getListByParameters(param);

    res.status(200).send({msg: "MAC : " + mac, menu: records});
});

app.listen(PORT, async function() {
    console.log('listened PORT: ' + PORT);
    dbConn = new DBHandler();
})

module.exports = app;