import sqlite3
import sys
from datetime import datetime


# DTO


class Vaccine:
    def __init__(self, id, date, supplier, quantity):
        self.id = id
        self.date = date
        self.supplier = supplier
        self.quantity = quantity


class Supplier:
    def __init__(self, id, name, logistic):
        self.id = id
        self.name = name
        self.logistic = logistic


class Clinic:
    def __init__(self, id, location, demand, logistic):
        self.id = id
        self.location = location
        self.demand = demand
        self.logistic = logistic


class Logistic:
    def __init__(self, id, name, count_sent, count_received):
        self.id = id
        self.name = name
        self.count_sent = count_sent
        self.count_received = count_received


# DAO
class _Vaccines:
    def __init__(self, connection):
        self._connection = connection

    def insert(self, vaccine):  # for received order
        with self._connection:
            c = self._connection.cursor()
            c.execute("""INSERT INTO vaccines (id, date, supplier, quantity) VALUES (?,?,?,?)""",
                      [vaccine.id, vaccine.date, vaccine.supplier, vaccine.quantity])

    def find(self, vaccine_id):
        with self._connection:
            c = self._connection.cursor()
            c.execute("""SELECT * FROM vaccines WHERE id = ?""", [vaccine_id])
            return Vaccine(*c.fetchone())

    def delete(self, vaccine_quantity):  # for sent order
        with self._connection:
            c = self._connection.cursor()
            c.execute("""DELETE FROM vaccines WHERE quantity = ?""", [vaccine_quantity])

    def updateQuantity(self, amount):  # for sent order
        with self._connection:
            amount = int(amount)
            c = self._connection.cursor()
            new_table = c.execute("""SELECT * FROM vaccines ORDER BY date ASC""").fetchall()
            for a in new_table:
                if a[3] < amount or a[3] == amount:
                    c.execute("""UPDATE vaccines SET quantity = ? WHERE id = ?""", [0, a[0]])
                    self._connection.commit()
                    amount = amount - a[3]
                elif a[3] > amount:
                    c.execute("""UPDATE vaccines SET quantity = ? WHERE id = ?""", [a[3] - amount, a[0]])
                    self._connection.commit()
                    amount = 0
                if amount == 0: break

    def sumOfQuantity(self):
        with self._connection:
            c = self._connection.cursor()
            amount = c.execute("""SELECT sum(quantity) FROM vaccines""").fetchone()
            return amount[0]


class _Suppliers:
    def __init__(self, connection):
        self._connection = connection

    def insert(self, supplier):
        with self._connection:
            c = self._connection.cursor()
            c.execute("""INSERT INTO suppliers (id, name, logistic) VALUES (?,?,?)""",
                      [supplier.id, supplier.name, supplier.logistic])

    def find(self, name_supplier):
        with self._connection:
            c = self._connection.cursor()
            tup = c.execute("""SELECT id FROM suppliers WHERE name = ?""", [name_supplier]).fetchone()
            return tup[0]


class _Clinics:
    def __init__(self, connection):
        self._connection = connection

    def insert(self, clinic):
        with self._connection:
            c = self._connection.cursor()
            c.execute("""INSERT INTO clinics (id, location, demand, logistic) VALUES (?,?,?,?)""",
                      [clinic.id, clinic.location, clinic.demand, clinic.logistic])

    def updateDemand(self, id, amount):  # for sent order
        with self._connection:
            c = self._connection.cursor()
            tup = c.execute("""SELECT demand FROM clinics WHERE id = ?""", [id]).fetchone()
            c.execute("""UPDATE clinics SET demand = ? WHERE id = ?""", [tup[0] - amount, id])

    def sumOfDemand(self):
        with self._connection:
            c = self._connection.cursor()
            amount = c.execute("""SELECT sum(demand) FROM clinics""").fetchone()
            return amount[0]

    def find(self, location):
        with self._connection:
            c = self._connection.cursor()
            tup = c.execute("""SELECT id,logistic FROM clinics WHERE location = ?""",
                            [location]).fetchall()
        return tup[0]


class _Logistics:
    def __init__(self, connection):
        self._connection = connection

    def insert(self, logistic):
        with self._connection:
            c = self._connection.cursor()
            c.execute("""INSERT INTO logistics (id, name, count_sent, count_received) VALUES (?,?,?,?)""",
                      [logistic.id, logistic.name, logistic.count_sent, logistic.count_received])

    def updateCountSent(self, id, amount):
        with self._connection:
            c = self._connection.cursor()
            tup = c.execute("""SELECT count_sent FROM logistics WHERE id = ?""", [id]).fetchone()
            c.execute("""UPDATE logistics SET count_sent = ? WHERE id = ?""", [tup[0] + amount, id])

    def updateCountReceived(self, suplier_id, amount):
        with self._connection:
            c = self._connection.cursor()
            logistic_id = c.execute("""SELECT logistic FROM suppliers WHERE id = ?""", [suplier_id]).fetchone()
            amount_logistic = c.execute("""SELECT count_received FROM logistics WHERE id = ?""",
                                        [logistic_id[0]]).fetchone()
            c.execute("""UPDATE logistics SET count_received = ? WHERE id = ?""",
                      [amount_logistic[0] + amount, logistic_id[0]])

    def totalRec_totalSen(self):
        with self._connection:
            c = self._connection.cursor()
            tuple = c.execute("""SELECT sum(count_received), sum(count_sent) FROM logistics""").fetchone()
            return tuple  # tuple[0] -> received  tuple[1]-> sent


# Repository

class Repository:

    def __init__(self):
        self._connection = sqlite3.connect('database.db')
        self.clinics = _Clinics(self._connection)
        self.suppliers = _Suppliers(self._connection)
        self.logistics = _Logistics(self._connection)
        self.vaccines = _Vaccines(self._connection)
        self.create_tables()

    def _close(self):
        self._connection.commit()
        self._connection.close()

    def create_tables(self):
        with self._connection:
            c = self._connection.cursor()
            c.executescript("""
            CREATE TABLE IF NOT EXISTS vaccines (
                id INTEGER PRIMARY KEY,
                date DATE NOT NULL,
                supplier INTEGER REFERENCES suppliers(id),
                quantity INTEGER NOT NULL
            );

            CREATE TABLE IF NOT EXISTS suppliers (
                id INTEGER PRIMARY KEY,
                name STRING NOT NULL,
                logistic INTEGER REFERENCES logistics(id)
            );

            CREATE TABLE IF NOT EXISTS clinics (
                id INTEGER PRIMARY KEY,
                location STRING NOT NULL,
                demand INTEGER NOT NULL,
                logistic INTEGER REFERENCES logistics(id)
            );

            CREATE TABLE IF NOT EXISTS logistics (
            id INTEGER PRIMARY KEY,
            name STRING NOT NULL,
            count_sent INTEGER NOT NULL,
            count_received INTEGER NOT NULL
            );
            
            CREATE TRIGGER IF NOT EXISTS delete_if_row_quantity_zero
            AFTER UPDATE ON vaccines
            BEGIN
            DELETE FROM vaccines WHERE quantity = 0;
            END;
        """)

    def receivedShipment(self, id, name, amount, date):
        supplier_id = self.suppliers.find(name)
        amount = int(amount)
        vac = Vaccine(id, date, supplier_id, amount)
        self.vaccines.insert(vac)
        self.logistics.updateCountReceived(supplier_id, amount)
        total_inventory = self.vaccines.sumOfQuantity()
        total_demand = self.clinics.sumOfDemand()
        tmp = self.logistics.totalRec_totalSen()
        total_received = tmp[0]
        total_sent = tmp[1]
        var = (total_inventory, total_demand, total_received, total_sent)
        return list(var)

    def sendShipment(self, location, amount):
        amount = int(amount)
        tup = self.clinics.find(location)
        clinic_id = tup[0]
        logistic_id = tup[1]
        self.clinics.updateDemand(clinic_id, amount)
        self.vaccines.updateQuantity(amount)
        self.logistics.updateCountSent(logistic_id, amount)
        total_inventory = self.vaccines.sumOfQuantity()
        total_demand = self.clinics.sumOfDemand()
        tmp = self.logistics.totalRec_totalSen()
        total_received = tmp[0]
        total_sent = tmp[1]
        tup = (total_inventory, total_demand, total_received, total_sent)
        return list(tup)


def main(argv):
    repo = Repository()
    configfile = argv[1]
    vaccines_ids = []
    with open(configfile, 'r', encoding='utf-8') as config:
        Lines = config.readlines()
        first_line = Lines[0].rstrip()
        first_line = first_line.rstrip()
        first_line = first_line.split(',')

        vaccinesNum = int(first_line[0])
        suppliersNum = int(first_line[1])
        clinicsNum = int(first_line[2])
        logisticsNum = int(first_line[3])
        low = len(Lines) - logisticsNum
        high = len(Lines)
        for i in range(low, high):
            new_logistic = Lines[i].rstrip()
            new_logistic = new_logistic.split(',')
            new = Logistic(*new_logistic)
            repo.logistics.insert(new)

        high = low
        low = len(Lines) - clinicsNum - logisticsNum
        for i in range(low, high):
            new_clinic = Lines[i].rstrip()
            new_clinic = new_clinic.split(',')
            new = Clinic(*new_clinic)
            repo.clinics.insert(new)

        high = low
        low = len(Lines) - clinicsNum - logisticsNum - suppliersNum
        for i in range(low, high):
            new_supplier = Lines[i].rstrip()
            new_supplier = new_supplier.split(',')
            new = Supplier(*new_supplier)
            repo.suppliers.insert(new)

        high = low
        low = 1
        for i in range(low, high):
            new_vaccine = Lines[i].rstrip()
            new_vaccine = new_vaccine.split(',')
            vaccines_ids.append(int(new_vaccine[0]))
            new = Vaccine(*new_vaccine)
            repo.vaccines.insert(new)

    orders_file = argv[2]
    outputs = []
    vaccines_counter = max(vaccines_ids)
    vaccines_counter = vaccines_counter + 1
    with open(orders_file, 'r', encoding='utf-8') as orders:
        Lines = orders.readlines()
        for line in Lines:
            line = line.rstrip()
            line = line.split(',')
            order_type = len(line)
            if order_type == 3:
                outputs.append(repo.receivedShipment(vaccines_counter, *line))
                vaccines_counter = vaccines_counter + 1
            else:
                outputs.append(repo.sendShipment(*line))

    with open(argv[3], "w") as out:
        for numbers in outputs:
            string_ints = [str(num) for num in numbers]
            str_of_ints = ",".join(string_ints)
            out.write(str_of_ints + '\n')


if __name__ == '__main__':
    main(sys.argv)
