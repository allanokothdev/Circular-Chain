/* eslint-disable require-jsdoc */
/* eslint-disable no-unused-vars */
/* eslint-disable max-len */

// Firebase
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

const ethers = require("ethers");
// FEVM RPC
const FilecoinRpc = "https://api.hyperspace.node.glif.io/rpc/v1";
const provider = new ethers.providers.JsonRpcProvider(FilecoinRpc);
const privateKey = "28c79b842a827fd27d66806d4662b259be4b23f4d732564c32ff900122bc0637";

const CircularChainABI = require("./abi/CircularChain.json");
const CircularChainContractAddress = "0x5aF7dA0f0d52F08f4852403f7EE49F79D96Fe1e3";


// CREATE NEW WALLET ADDRESS
exports.createWallet = functions.https.onCall(() => {
  const wallet = ethers.Wallet.createRandom();
  console.log(wallet.address);
  return {
    address: wallet.address,
    privateKey: wallet.privateKey,
  };
});

// FETCH SUPPLY CHAIN STAGES
exports.fetchStages = functions.https.onCall(async (_data, context) => {
  try {
    const batchId = _data.batch;
    const signer = new ethers.Wallet(privateKey, provider);
    const circularContract = new ethers.Contract(CircularChainContractAddress, CircularChainABI, signer);
    // fetching the product stages using their batch ID from the Circular Chain contracts
    const response = await circularContract.fetchBatchStages(batchId);
    const stages = [];
    await Promise.all(response.map(async (i) => {
      const item = {
        stageId: i.stageId.toNumber().toString(),
        title: i.title,
        summary: i.summary,
        publisher: i.publisher,
        timestamp: i.timestamp.toNumber(),
        location: i.location,
        esgScore: i.esgScore,
        batchId: i.batchId.toNumber.toString(),
      };
      stages.push(item);
    }));

    return stages;
  } catch (error) {
    console.error(error);
    // Re-throwing the error as an HttpsError so that the client gets the error details.
    throw new functions.https.HttpsError("unknown", error.message, error);
  }
});


// FETCH SUPPLY CHAIN BATCH STAGES
exports.fetchBatchDetails = functions.https.onCall(async (data, context) => {
  try {
    const batchId = data.batch;
    const signer = new ethers.Wallet(privateKey, provider);
    const circularContract = new ethers.Contract(CircularChainContractAddress, CircularChainABI, signer);
    // fetching the respective batch details
    const response = await circularContract.fetchBatchDetails(batchId);

    return response;
  } catch (error) {
    console.error(error);
    // Re-throwing the error as an HttpsError so that the client gets the error details.
    throw new functions.https.HttpsError("unknown", error.message, error);
  }
});


// FETCH SUPPLY CHAIN STAGES
exports.calculateAggregateESGScore = functions.https.onCall(async (data, context) => {
  try {
    const batchId = data.batch;
    const signer = new ethers.Wallet(privateKey, provider);
    const circularContract = new ethers.Contract(CircularChainContractAddress, CircularChainABI, signer);
    // Fetch Aggregate Sustainability Score
    const response = await circularContract.calculateAggregateESGScore(batchId);

    return {
      esg: response.esg,
      noOfItems: response.noOfItems.toNumber(),
    };
  } catch (error) {
    console.error(error);
    // Re-throwing the error as an HttpsError so that the client gets the error details.
    throw new functions.https.HttpsError("unknown", error.message, error);
  }
});

// ADD NEW PRODUCT BATCH
exports.createBatch = functions.https.onCall(async (data, context) => {
  try {
    const brandId = data.brandId;
    const stakeholders = data.stakeholders;
    const signer = new ethers.Wallet(privateKey, provider);
    const circularContract = new ethers.Contract(CircularChainContractAddress, CircularChainABI, signer);
    // Create New Batch
    const transaction = await circularContract.createBatch(brandId, stakeholders);
    const tx = await transaction.wait();

    return "data";
  } catch (error) {
    console.error(error);
    // Re-throwing the error as an HttpsError so that the client gets the error details.
    throw new functions.https.HttpsError("unknown", error.message, error);
  }
});

exports.addNewStage = functions.https.onCall(async (data, context) => {
  try {
    const address = data.address;
    const batchId = data.batch;
    const title = data.title;
    const summary = data.summary;
    const location = data.location;
    const natureScore = data.natureScore;
    const climateScore = data.climateScore;
    const labourScore = data.labourScore;
    const communityScore = data.communityScore;
    const wasteScore = data.wasteScore;
    const signer = new ethers.Wallet(privateKey, provider);
    const circularContract = new ethers.Contract(CircularChainContractAddress, CircularChainABI, signer);
    // Add New Supply Chain Stage
    const transaction = await circularContract.addNewStage(batchId, title, summary, location, [natureScore, climateScore, labourScore, communityScore, wasteScore]);
    const tx = await transaction.wait();
    return "Success";
  } catch (error) {
    console.error(error);
    // Re-throwing the error as an HttpsError so that the client gets the error details.
    throw new functions.https.HttpsError("unknown", error.message, error);
  }
});
