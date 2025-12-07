db = db.getSiblingDB('mainDB');
db.createUser({
  user: "springuser",
  pwd: "secret",
  roles: [{ role: "readWrite", db: "mainDB" }]
});