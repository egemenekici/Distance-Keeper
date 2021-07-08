const { Client } = require('./Client')

class DBHandler {
	constructor() {}
	
	async getListByParameters(param){
		const clientInstance = Client.getInstance();
		const clientConn = await clientInstance.clientOpen();
		if(clientConn == null) return({err: "DBConnectionError"}); 
		
		let menu = [];
		menu = new Promise((resolve, reject) => {
            clientConn.db("distkeep").collection("menu").find(param).toArray((err, items) => {
                if(err) resolve(null);
				resolve(items);
            });
		});
		return menu;
	}
}



module.exports = {
	DBHandler
}