// Connect to the admin database
db = db.getSiblingDB('admin');

// Create an admin user
db.createUser({
    user: 'adminUser',
    pwd: 'adminPassword',
    roles: [
        { role: 'userAdminAnyDatabase', db: 'admin' },
        { role: 'readWriteAnyDatabase', db: 'admin' },
        { role: 'dbAdminAnyDatabase', db: 'admin' }
    ]
});

// Switch to the product-service database
db = db.getSiblingDB('product-service');

// Create a user for the product-service database


/*db.createUser({
    user: 'productUser',
    pwd: 'productPassword',
    roles: [
        { role: 'readWrite', db: 'product-service' },
        { role: 'dbAdmin', db: 'product-service' }
    ]
});*/

db.user.insert({
    username: "productUser",
    password: "productPassword"
});


// Switch back to the admin database
db = db.getSiblingDB('admin');
