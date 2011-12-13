


class result(object):
    def __init__(self, value, start, end):
        self.value, self.start, self.end = value, start, end

    def __str__(self):
        return "<result: '%s', [%d:%d]>" % (self.value, self.start, self.end)

    def __repr__(self):
        return str(self)

def word(w):
    def _parser(s, i):
        if s[i:].startswith(w):
            yield result(w, i, i + len(w))
    return _parser

def any(*args):
    def _parser(s, i):
        for p in args:
            for r in p(s, i):
                yield r
    return _parser

def seq(*args):
    def _parser(s, i):
        pass
    return _parser

m = any(word("piet"), word("piet blaat"))


s = "piet blaat aap"

print list(m(s, 0))

