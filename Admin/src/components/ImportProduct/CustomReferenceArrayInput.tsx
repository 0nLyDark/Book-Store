import * as React from "react";
import {
  AutocompleteArrayInput,
  useDataProvider,
  useInput,
  FieldTitle,
  RaRecord,
  ListContextProvider,
  ListControllerSuccessResult,
  Labeled,
} from "react-admin";
import { useState, useEffect, useMemo, useRef } from "react";
import TextField from "@mui/material/TextField";
import { AutocompleteRenderInputParams } from "@mui/material";

interface CustomReferenceArrayInputProps {
  source: string;
  reference: string;
  optionText: string | ((choice: RaRecord) => string);
  label: string;
}

const PER_PAGE = 100;

const CustomReferenceArrayInput: React.FC<CustomReferenceArrayInputProps> = ({
  source,
  reference,
  label,
  optionText,
}) => {
  const dataProvider = useDataProvider();
  const { field, fieldState } = useInput({ source });

  const ids = Array.isArray(field.value) ? field.value : [];

  const [selectedChoices, setSelectedChoices] = useState<RaRecord[]>([]);
  const [filter, setFilter] = useState<{ q?: string }>({});
  const [page, setPage] = useState<number>(1);
  const [choices, setChoices] = useState<RaRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState<number | null>(null);
  const [lastPage, setLastPage] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);

  const loadingRef = useRef(false);
  const lastPageRef = useRef(false);

  // Load selected items by ids
  useEffect(() => {
    if (ids.length === 0) {
      setSelectedChoices([]);
      return;
    }

    setLoadError(null); // Reset error

    dataProvider
      .getMany(reference, { ids })
      .then(({ data }) => {
        setSelectedChoices(data);
      })
      .catch(async (error) => {
        console.warn("getMany failed, fallback to getOne per ID:", error);
        const validRecords: RaRecord[] = [];
        let hasError = false;

        for (const id of ids) {
          try {
            const { data } = await dataProvider.getOne(reference, { id });
            validRecords.push(data);
          } catch (err) {
            console.warn(`getOne failed for id = ${id}`, err);
            hasError = true;
          }
        }

        setSelectedChoices(validRecords);
        if (hasError) {
          setLoadError("Một số mục đã chọn không tồn tại hoặc bị lỗi.");
        }
      });
  }, [dataProvider, ids, reference]);

  // Load first page on mount or filter change
  useEffect(() => {
    async function fetchData() {
      if (loadingRef.current || lastPageRef.current) return;

      loadingRef.current = true;
      setLoading(true);
      setLoadError(null);

      try {
        const response = await dataProvider.getList(reference, {
          pagination: { page: 0, perPage: PER_PAGE },
          sort: { field: "id", order: "ASC" },
          filter,
        });

        setChoices(response.data);
        setTotal(typeof response.total === "number" ? response.total : 0);
        const isLast = (response as any).json?.lastPage ?? false;
        setLastPage(isLast);
        lastPageRef.current = isLast;
        setPage(1);
      } catch (error) {
        console.error("Failed to load data", error);
        setLoadError("Không thể tải dữ liệu.");
      } finally {
        loadingRef.current = false;
        setLoading(false);
      }
    }

    fetchData();
  }, [dataProvider, reference, filter]);

  // Load next page when user scrolls
  useEffect(() => {
    if (page === 1) return;

    async function loadMore() {
      if (loadingRef.current || lastPageRef.current) return;

      loadingRef.current = true;
      setLoading(true);

      try {
        const response = await dataProvider.getList(reference, {
          pagination: { page: page - 1, perPage: PER_PAGE },
          sort: { field: "id", order: "ASC" },
          filter,
        });

        setChoices((prev) => {
          const map = new Map(prev.map((item) => [item.id, item]));
          response.data.forEach((item: RaRecord) => map.set(item.id, item));
          return Array.from(map.values());
        });

        const isLast = (response as any).json?.lastPage ?? false;
        setLastPage(isLast);
        lastPageRef.current = isLast;
        setTotal(typeof response.total === "number" ? response.total : 0);
      } catch (error) {
        console.error("Failed to load more data", error);
        setLoadError("Không thể tải thêm dữ liệu.");
      } finally {
        loadingRef.current = false;
        setLoading(false);
      }
    }

    loadMore();
  }, [page, dataProvider, reference, filter]);

  const mergedChoices = useMemo(() => {
    const map = new Map<string | number, RaRecord>();
    choices.forEach((item) => item?.id !== undefined && map.set(item.id, item));
    selectedChoices.forEach((item) => {
      if (item?.id !== undefined && !map.has(item.id)) {
        map.set(item.id, item);
      }
    });
    return Array.from(map.values());
  }, [choices, selectedChoices]);

  const handleFilterChange = (searchText: string) => {
    setFilter(searchText ? { q: searchText } : {});
    setPage(1);
    setChoices([]);
    setTotal(null);
    setLastPage(false);
    lastPageRef.current = false;
  };

  const handleScroll = (event: React.UIEvent<HTMLDivElement>) => {
    const target = event.currentTarget;
    const nearBottom =
      target.scrollTop + target.clientHeight >= target.scrollHeight - 50;
    const hasMore = !lastPage && (total === null || choices.length < total);

    if (nearBottom && hasMore && !loading) {
      setPage((prevPage) => prevPage + 1);
    }
  };

  const listContext: ListControllerSuccessResult = {
    data: mergedChoices,
    isLoading: loading,
    isFetching: loading,
    isPending: false,
    resource: reference,
    total: total ?? 0,
    page,
    perPage: PER_PAGE,
    sort: { field: "id", order: "ASC" },
    setPage,
    setPerPage: () => setPage(1),
    hasNextPage: !lastPage,
    hasPreviousPage: page > 1,
    error: null,
    selectedIds: [],
    onSelect: () => {},
    onToggleItem: () => {},
    onUnselectItems: () => {},
    onSelectAll: () => {},
    filterValues: filter,
    displayedFilters: {},
    setFilters: () => {},
    showFilter: () => {},
    hideFilter: () => {},
    setSort: () => {},
    refetch: () => Promise.resolve(),
  };

  return (
    <ListContextProvider value={listContext}>
      <Labeled label={<FieldTitle label={label} source={source} />}>
        <AutocompleteArrayInput
          {...field}
          choices={mergedChoices}
          optionText={optionText}
          disabled={loading}
          filterToQuery={(searchText) => ({ q: searchText })}
          // @ts-ignore: react-admin chưa expose prop onScrollDropdown
          onScrollDropdown={handleScroll}
          onSearchChange={handleFilterChange}
          renderInput={(params: AutocompleteRenderInputParams) => (
            <TextField
              {...params}
              error={Boolean(fieldState.error) || Boolean(loadError)}
              helperText={fieldState.error?.message || loadError || ""}
              fullWidth
            />
          )}
        />
      </Labeled>
    </ListContextProvider>
  );
};

export default CustomReferenceArrayInput;
