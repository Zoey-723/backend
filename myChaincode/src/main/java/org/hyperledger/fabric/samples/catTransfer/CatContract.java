/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.catTransfer;


import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import com.owlike.genson.Genson;

@Contract(
        name = "basic",
        info = @Info(
                title = "Cat Contract",
                description = "The hyperlegendary cat contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "a.transfer@example.com",
                        name = "Adrian Transfer",
                        url = "https://hyperledger.example.com")))
@Default
public final class CatContract implements ContractInterface {
    private final Genson genson = new Genson();
    private enum CatContractErrors {
        Cat_NOT_FOUND,
        Cat_ALREADY_EXISTS
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        for (int i = 0; i < 10; i++) {
            createCat(ctx, "cat-" + i, i, "橘猫", "橘黄色");
        }
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Cat createCat(final Context ctx, final String catName, final int catAge,
                         final String catBreed, final String catColor) {
        ChaincodeStub stub = ctx.getStub();

        if (CatExists(ctx, catName)) {
            String errorMessage = String.format("Cat %s already exists", catName);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CatContractErrors.Cat_ALREADY_EXISTS.toString());
        }

        Cat cat = new Cat(catName, catAge, catColor, catBreed);
        // Use Genson to convert the Cat into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(cat);
        stub.putStringState(catName, sortedJson);

        return cat;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Cat ReadCat(final Context ctx, final String catName) {
        ChaincodeStub stub = ctx.getStub();
        String catJSON = stub.getStringState(catName);

        if (catJSON == null || catJSON.isEmpty()) {
            String errorMessage = String.format("Cat %s does not exist", catName);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CatContractErrors.Cat_NOT_FOUND.toString());
        }

        Cat cat = genson.deserialize(catJSON, Cat.class);
        return cat;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Cat UpdateCat(final Context ctx, final String catName, final int catAge,
                         final String catBreed, final String catColor) {
        ChaincodeStub stub = ctx.getStub();

        if (!CatExists(ctx, catName)) {
            String errorMessage = String.format("Cat %s does not exist", catName);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CatContractErrors.Cat_NOT_FOUND.toString());
        }

        Cat newCat = new Cat(catName, catAge, catColor, catBreed);
        // Use Genson to convert the Cat into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(newCat);
        stub.putStringState(catName, sortedJson);
        return newCat;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteCat(final Context ctx, final String catName) {
        ChaincodeStub stub = ctx.getStub();

        if (!CatExists(ctx, catName)) {
            String errorMessage = String.format("Cat %s does not exist", catName);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CatContractErrors.Cat_NOT_FOUND.toString());
        }

        stub.delState(catName);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean CatExists(final Context ctx, final String catName) {
        ChaincodeStub stub = ctx.getStub();
        String catJSON = stub.getStringState(catName);

        return (catJSON != null && !catJSON.isEmpty());
    }



}
