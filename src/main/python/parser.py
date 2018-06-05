

def split_string(text, separator, quote):
    in_quote = False
    tokens = []
    token = ""
    
    for c in text:
        if not in_quote:
        
            if c == separator:
                if len(token) > 0: tokens.append(token)
                token = ""
        
            else:
                if c == quote: in_quote = not in_quote
                token += c
            
        else:
            if c == quote: in_quote = not in_quote
            token += c
            
    if len(token) > 0: tokens.append(token)
    
    return tokens if not in_quote else None



class tree_node:
    def __init__(self, _rule):
        self.rule = _rule
        self.left = None
        self.right = None
        self.terminal = ""
        self.pre_event = None
        self.post_event = None
    
    def get_text_recursive(self, node):
        text = ""
        if len(node.terminal) == 0:
            text = node.get_text_recursive(node.left)
            text += node.get_text_recursive(node.right)
        else:
            text = node.terminal
        return text
    
    
        
    def get_text(self):
        return self.get_text_recursive(self)


class parser:
    def is_terminal(self, token):
        tks = token.split(self.quote)
        if len(tks) not in [1, 3]: exit() # exception
        
        if len(tks) == 1: return False
    
        if token[0] == self.quote and token[-1] == self.quote: return True

        exit() # exception
        
    
        
    def add_terminal(self, text):
        text = text.strip(self.quote)
        t_rules = []
        for c in text:
            if c not in self.T_to_NT: self.T_to_NT[c] = []
            self.T_to_NT[c].append(self.free_number)
            t_rules.append(self.free_number)
            self.free_number += 1
        
        while len(t_rules) > 1:
            p2_NF = t_rules.pop()
            p1_NF = t_rules.pop()
            
            n = self.free_number
            self.free_number += 1
            
            if (p1_NF, p2_NF) not in self.NT_to_NT: self.NT_to_NT[(p1_NF, p2_NF)] = []
            self.NT_to_NT[(p1_NF, p2_NF)].append(n)
            
            t_rules.append(n)
        return t_rules[0]
    
        
    
    def __init__(self, lines, _events = {}, _quote = "\""):
        self.free_number = 0
        self.rule_to_NT = {}
        self.T_to_NT = {}
        self.NT_to_NT = {}
        self.quote = _quote
        self.wort_tree = None
        self.word_in_grammer = False
        self.events = _events
        self.NT_to_rule = {}

        for line in lines:
            
            tokens_level_1 = [t.strip(" ") for t in split_string(line, "=", self.quote)]
            if tokens_level_1 == None or len(tokens_level_1) != 2: exit() # exception


            rule = tokens_level_1[0]
            products = [p.strip(" ") for p in split_string(tokens_level_1[1], "|", self.quote)]
            

            if rule not in self.rule_to_NT:
                self.rule_to_NT[rule] = self.free_number
                self.free_number += 1
                
            rule_NT = self.rule_to_NT[rule]
            self.NT_to_rule[rule_NT] = rule
            
            
            
            
            for product in products:
                single_NTs = [NT.strip(" ") for NT in split_string(product, " ", self.quote)]
                
                
                # changing all (non)terminals into rule numbers
                for i in range(len(single_NTs)):
                    if self.is_terminal(single_NTs[i]):
                        single_NTs[i] = self.add_terminal(single_NTs[i])
                    else:
                        if single_NTs[i] not in self.rule_to_NT:
                            self.rule_to_NT[single_NTs[i]] = self.free_number
                            self.free_number += 1
                        single_NTs[i] = self.rule_to_NT[single_NTs[i]]
                
                
                # more than two rules
                while len(single_NTs) > 2:
                    p2_NF = single_NTs.pop()
                    p1_NF = single_NTs.pop()
                    
                    n = self.free_number
                    self.free_number += 1
                    
                    if (p1_NF, p2_NF) not in self.NT_to_NT: self.NT_to_NT[(p1_NF, p2_NF)] = []
                    self.NT_to_NT[(p1_NF, p2_NF)].append(n)
                    
                    single_NTs.append(n)
                    
                
                    
                # two product rules
                if len(single_NTs) == 2:
                    p1_NF = single_NTs[0]
                    p2_NF = single_NTs[1]
                    if (p1_NF, p2_NF) not in self.NT_to_NT: self.NT_to_NT[(p1_NF, p2_NF)] = []
                    self.NT_to_NT[(p1_NF, p2_NF)].append(rule_NT)
                
                
                
                # only one product rule
                elif len(single_NTs) == 1:
                    p1_NF = single_NTs[0]
                    if (p1_NF) not in self.NT_to_NT: self.NT_to_NT[(p1_NF)] = []
                    self.NT_to_NT[(p1_NF)].append(rule_NT)
                   
        
        
    # adding singleton rules, e.g. S -> A, A -> B, B -> C
    def collect_backward(self, r1):
        collection = [r1]
        i = 0
        while i < len(collection):
            r = collection[i]
            if (r) in self.NT_to_NT:
                for rf in self.NT_to_NT[(r)]:
                    collection.append(rf)
            i += 1
        return collection
    
    
    # filling the syntax tree including lexers and events
    def fill_tree(self, node, dp, i, j):
        if node.rule in self.NT_to_rule:
            pre_event_name = self.NT_to_rule[node.rule] + "_pre_event"
            post_event_name = self.NT_to_rule[node.rule] + "_post_event"
            
            if pre_event_name in self.events:
                node.pre_event = self.events[pre_event_name]
            if post_event_name in self.events:
                node.post_event = self.events[post_event_name]
        
        if i == 0:
            node.terminal = dp[i][j][node.rule]
        else:
            node.left = tree_node(dp[i][j][node.rule][0])
            node.right = tree_node(dp[i][j][node.rule][1])
            l, r = dp[i][j][node.rule][2]
            self.fill_tree(node.left, dp, l, r)
            l, r = dp[i][j][node.rule][3]
            self.fill_tree(node.right, dp, l, r)
            
            
            
        
    def raise_events_recursive(self, node):
        
        # raise event here
        if node.pre_event != None: node.pre_event(node)
        
        if len(node.terminal) == 0:
            self.raise_events_recursive(node.left)
            self.raise_events_recursive(node.right)
            
        if node.post_event != None: node.post_event(node)
                
                
    
    def raise_events(self):
        if self.wort_tree != None: self.raise_events_recursive(self.wort_tree)
        
        
    
    # re-implementation of Cocke-Younger-Kasami algorithm
    def parse(self, text):
        n = len(text)
        dp = [[{} for ii in text] for i in text]
        
        for i, c in enumerate(text):
            if c not in self.T_to_NT: return
            for r in self.T_to_NT[c]:
                for rf in self.collect_backward(r):
                    dp[0][i][rf] = (c)
                
        
        for i in range(1, n):
            for j in range(n - i):
                for k in range(i):
                    for r1 in dp[k][j]:
                        for r2 in dp[i - k - 1][j + k + 1]:
                            if (r1, r2) in self.NT_to_NT:
                                for r in self.NT_to_NT[(r1, r2)]:
                                    for rf in self.collect_backward(r):
                                        dp[i][j][rf] = (r1, r2, (k, j), (i - k - 1, j + k + 1))
         
        self.word_in_grammer = (0) in dp[-1][0]
        
        if self.word_in_grammer:
            self.wort_tree = tree_node(0)
            self.fill_tree(self.wort_tree, dp, len(dp) - 1, 0)
        else:
            self.wort_tree = None
        
        
        
        
        
def HG_LPL_pre_event(node):
    print("headgroup: %s" % node.get_text())
        
        
def HG_PL_pre_event(node):
    print("headgroup: %s" % node.get_text())
        
        
def Carbon_pre_event(node):
    print("carbon length: %s" % node.get_text())
    
    
def DB_pre_event(node):
    print("DB length: %s" % node.get_text())
    

G = []
with open("grammer.txt") as infile:
    for line in infile:
        if line[0] == "#": continue
        G.append(line.strip())
        
        
events = {"Carbon_pre_event": Carbon_pre_event, "DB_pre_event": DB_pre_event, "HG_PL_pre_event": HG_PL_pre_event, "HG_LPL_pre_event": HG_LPL_pre_event}
        
      

w = "LPA 6:1"
p = parser(G, events, "\"")
p.parse(w)
print(p.word_in_grammer)
p.raise_events()